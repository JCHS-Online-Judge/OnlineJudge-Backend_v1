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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Judge {

	private static final JudgeInfo judgeInfo = JudgeInfo.getJudgeInfo();

	public void init() {
		log.info("[Judge] Init.");
		judgeInfo.updateState(JudgeInfo.ExceptionType.WAITING);
	}

	@SneakyThrows(IOException.class)
	public void writeSource() {
		File file = new File("./Main.%s".formatted(judgeInfo.getLanguage().getExtension()));
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		writer.write(judgeInfo.getSource());
		writer.close();

		log.info("[Judge] Source Write Success.");
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
				sb.setLength(sb.length() - 1);
			}
			if (!sb.isEmpty()) {
				log.error("[Judge] Compile Error - %s".formatted(sb.toString()));
				judgeInfo.updateState(JudgeInfo.ExceptionType.COMPILE_ERROR, sb.toString());
			} else {
				log.info("[Judge] Compile Success.");
			}
		}
	}

	@SneakyThrows({InterruptedException.class, ExecutionException.class})
	public void run() {
		List<Callable<List<Long>>> tasks = new ArrayList<>();

		AtomicInteger correctCnt = new AtomicInteger();

		List<JudgeInfo.TestCase> testCases = judgeInfo.getTestCases();

		for (int i = 0; i < testCases.size(); i++) {
			JudgeInfo.TestCase testCase = testCases.get(i);

			int finalI = i;
			tasks.add(() -> {
				try {
					log.info("[#%02d] Judge Start.".formatted(finalI));

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

								try (BufferedReader reader = new BufferedReader(
										new InputStreamReader(watchdogProcess.getInputStream()))) {
									String line;
									boolean check = false;

									while ((line = reader.readLine()) != null) {
										try {
											maxMem[0] = Math.max(maxMem[0], Long.parseLong(line.trim()));
											check = true;
										} catch (NumberFormatException ignored) {
										}
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
					} catch (Exception ignored) {
					}

					StringBuilder error = new StringBuilder();
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
						String line;
						while ((line = reader.readLine()) != null) {
							error.append(line).append('\n');
						}
					}
					if (!error.isEmpty()) {
						error.setLength(error.length() - 1);
					}
					if (!error.isEmpty()) {
						log.error("[#%02d] Runtime Error - %s".formatted(finalI, error.toString()));
						judgeInfo.updateState(JudgeInfo.ExceptionType.RUNTIME_ERROR, error.toString());
					}

					StringBuilder output = new StringBuilder();
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
						String line;
						while ((line = reader.readLine()) != null) {
							output.append(line).append('\n');
						}
					}
					if (!output.isEmpty()) {
						output.setLength(output.length() - 1);
					}
					if (!output.toString().equals(testCase.getOutput())) {
						log.warn("[#%02d] Wrong Answer - (Expected: %s, Real: %s)".formatted(finalI, testCase.getOutput(), output.toString()));
						judgeInfo.updateState(JudgeInfo.ExceptionType.WRONG_ANSWER);
					} else {
						correctCnt.incrementAndGet();
						log.info("[#%02d] Correct Answer (%.0f%%)".formatted(finalI, (float) correctCnt.get() / JudgeInfo.getJudgeInfo().getTestCases().size() * 100));
						judgeInfo.updateState(JudgeInfo.ExceptionType.PROCESSING, correctCnt.get(),
								JudgeInfo.getJudgeInfo().getTestCases().size());
					}

					submit.cancel(true);

					if (maxMem[0] > judgeInfo.getMemoryLimit() * 1024) {
						log.error("[#%02d] Memory Exceed.".formatted(finalI));
						judgeInfo.updateState(JudgeInfo.ExceptionType.MEMORY_LIMIT);
					}

					return List.of(
							System.currentTimeMillis() - startTime,
							maxMem[0]
					);
				} catch (IOException e) {
					e.printStackTrace();
				}

				return List.of(0L, 0L);
			});
		}

		long timeSum = 0, memorySum = 0;

		ExecutorService executor = Executors.newSingleThreadExecutor();

		for (Callable<List<Long>> task : tasks) {
			try {
				Future<List<Long>> future = executor.submit(task);
				List<Long> result = future.get(judgeInfo.getTimeLimit(), TimeUnit.MILLISECONDS);

				timeSum += result.get(0);
				memorySum += result.get(1);
			} catch (TimeoutException e) {
				log.error("[Judge] Time Exceed.");
				judgeInfo.updateState(JudgeInfo.ExceptionType.TIME_LIMIT);
			}
		}

		timeSum /= tasks.size();
		memorySum /= tasks.size();

		log.error("[Judge] All Correctly. (TIME=%s, MEMORY=%s)".formatted(timeSum, memorySum));
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
