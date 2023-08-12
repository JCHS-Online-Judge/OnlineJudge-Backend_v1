package com.github.ioloolo.onlinejudge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

public class Judge {

	private static final JudgeInfo judgeInfo = JudgeInfo.getJudgeInfo();

	public void init() {
		judgeInfo.updateState(JudgeInfo.ExceptionType.WAITING);
	}

	@SneakyThrows(IOException.class)
	public void writeSource() {
		File file = new File("./Main.%s".formatted(judgeInfo.getLanguage().getExtension()));
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		writer.write(judgeInfo.getSource());
		writer.close();
	}

	@SneakyThrows(IOException.class)
	public void compile() {
		if (judgeInfo.getLanguage().getCompile() != null) {
			Process process = new ProcessBuilder()
					.command("sh", "-c", judgeInfo.getLanguage().getCompile())
					.start();

			StringBuilder sb = new StringBuilder();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append('\n');
				}
			}
			if (!sb.isEmpty()) {
				judgeInfo.updateState(JudgeInfo.ExceptionType.COMPILE_ERROR, sb.toString());
			}
		}
	}

	@SneakyThrows({InterruptedException.class, ExecutionException.class})
	public void run() {
		List<Callable<List<Long>>> tasks = new ArrayList<>();

		for (JudgeInfo.TestCase testCase : judgeInfo.getTestCases()) {
			tasks.add(() -> {
				Process process = new ProcessBuilder()
						.command("sh", "-c", judgeInfo.getLanguage().getRun())
						.start();

				long startTime = System.currentTimeMillis();
				final long[] maxMem = {0};

				//noinspection unchecked
				Future<Void> submit = (Future<Void>)Executors.newSingleThreadExecutor().submit(() -> {
					while (true) {
						try {
							Process watchdogProcess = new ProcessBuilder()
									.command("sh", "-c", "ps -p %d -o rss".formatted(process.pid()))
									.start();

							try (BufferedReader reader = new BufferedReader(new InputStreamReader(watchdogProcess.getInputStream()))) {
								String line;
								boolean check = false;

								while ((line = reader.readLine()) != null) {
									try {
										maxMem[0] = Math.max(maxMem[0], Long.parseLong(line.trim()));
										check = true;
									} catch (NumberFormatException ignored) {}
								}

								if (!check) {
									break;
								}
							}
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				});

				try (OutputStream input = process.getOutputStream()) {
					input.write(testCase.getInput().getBytes(StandardCharsets.UTF_8));
				}

				StringBuilder error = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						error.append(line).append('\n');
					}
				}
				if (!error.isEmpty()) {
					judgeInfo.updateState(JudgeInfo.ExceptionType.RUNTIME_ERROR, error.toString());
				}

				StringBuilder output = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						output.append(line).append('\n');
					}
				}
				if (!output.toString().equals(testCase.getOutput())) {
					judgeInfo.updateState(JudgeInfo.ExceptionType.WRONG_ANSWER);
				}

				submit.cancel(true);

				if (maxMem[0] > judgeInfo.getMemoryLimit()*1024) {
					judgeInfo.updateState(JudgeInfo.ExceptionType.MEMORY_LIMIT);
				}

				return List.of(
						System.currentTimeMillis() - startTime,
						maxMem[0]
				);
			});
		}

		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		long timeSum = 0, memorySum = 0;

		for (Future<List<Long>> future : executor.invokeAll(tasks, judgeInfo.getTimeLimit(), TimeUnit.MILLISECONDS)) {
			try {
				List<Long> result = future.get();

				timeSum += result.get(0);
				memorySum += result.get(1);
			} catch (CancellationException e) {
				judgeInfo.updateState(JudgeInfo.ExceptionType.TIME_LIMIT);
			}
		}

		timeSum /= tasks.size();
		memorySum /= tasks.size();

		judgeInfo.updateState(JudgeInfo.ExceptionType.CORRECT, timeSum, memorySum);
	}

	public static void main(String[] args) {
		Judge judge = new Judge();

		judge.init();
		judge.writeSource();
		judge.compile();
		judge.run();
	}
}
