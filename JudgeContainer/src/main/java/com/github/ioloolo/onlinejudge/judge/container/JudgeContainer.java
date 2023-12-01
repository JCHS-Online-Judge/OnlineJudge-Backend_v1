package com.github.ioloolo.onlinejudge.judge.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;

import com.github.ioloolo.onlinejudge.judge.container.data.JudgeLanguage;
import com.github.ioloolo.onlinejudge.judge.container.data.JudgeResult;
import com.github.ioloolo.onlinejudge.judge.container.exception.CompileException;
import com.github.ioloolo.onlinejudge.judge.container.exception.JudgeRuntimeException;
import com.github.ioloolo.onlinejudge.judge.container.exception.MemoryLimitException;
import com.github.ioloolo.onlinejudge.judge.container.exception.TimeLimitException;
import com.github.ioloolo.onlinejudge.judge.container.exception.WrongAnswerException;
import com.github.ioloolo.onlinejudge.judge.container.util.MongoBridge;
import com.github.ioloolo.onlinejudge.judge.container.util.ProcessEnvironment;
import com.mongodb.DBRef;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class JudgeContainer {

	private static final ProcessEnvironment processEnvironment = ProcessEnvironment.getInstance();
	private static final MongoBridge        mongoBridge        = MongoBridge.getInstance();

	private Document judgeInfo;

	private JudgeContainer() {

		processEnvironment.init();
		mongoBridge.init();
	}

	public static void judge() throws Exception {

		JudgeContainer judgeContainer = new JudgeContainer();

		judgeContainer.fetchJudgeInfo();
		judgeContainer.compile();
		judgeContainer.runTestCases();
	}

	public static void main(String[] args) {

		try {
			JudgeContainer.judge();
		} catch (Exception e) {
			log.error("[Exception] " + e.getMessage());

			System.exit(0);
		}
	}

	private void fetchJudgeInfo() {

		String id = processEnvironment.getArgumentInfo().getJudgeId();
		Document judgeInfo = mongoBridge.findById(id);

		if (judgeInfo == null) {
			throw new IllegalArgumentException("JudgeInfo not found.");
		}

		setJudgeInfo(judgeInfo);
		setResult(JudgeResult.READY);

		log.info("[Judge] JudgeInfo fetch success.");
	}

	private void compile() throws Exception {

		String sourceCode = judgeInfo.getString("sourceCode");
		JudgeLanguage language = JudgeLanguage.valueOf(judgeInfo.getString("language"));

		// #1. Write source code to file
		Path sourceCodePath = Paths.get(language.getFileName());
		if (Files.exists(sourceCodePath)) {
			Files.delete(sourceCodePath);
		}
		Files.writeString(sourceCodePath, sourceCode, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);

		log.info("[Judge] Source code write success.");

		// #2. Compile
		if (language.getCompile() == null) {
			log.info("[Judge] Compile not required.");
			return;
		}

		log.info("[Judge] Compile start.");

		Process process = new ProcessBuilder().command("sh", "-c", language.getCompile()).start();

		StringBuilder sb = new StringBuilder();

		try (
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))
		) {
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
			setResult(JudgeResult.COMPILE_ERROR, Map.of("message", sb.toString()));
			throw new CompileException();
		} else {
			log.info("[Judge] Compile Success.");
		}
	}

	private void runTestCases() throws Exception {

		JudgeLanguage language = JudgeLanguage.valueOf(judgeInfo.getString("language"));
		Document problem = mongoBridge.reference(judgeInfo.get("problem", DBRef.class));

		List<Document> testCases = problem.getList("testCases", Document.class)
				.stream()
				.filter(testCase -> !testCase.getBoolean("isSample"))
				.toList();

		setResult(JudgeResult.PROCESSING);

		// Create thread pool
		ExecutorService judgeThread = Executors.newSingleThreadExecutor();

		AtomicLong sumTime = new AtomicLong(0);
		AtomicLong sumMemory = new AtomicLong(0);

		// Run test cases
		for (int i = 0; i < testCases.size(); i++) {
			Document testCase = testCases.get(i);

			log.info("[Judge] Run test case (%d/%d).".formatted(i + 1, testCases.size()));

			Future<Optional<? extends Exception>> judgeTask = judgeThread.submit(() -> {

				// Create thread pool for memory check
				ScheduledExecutorService memoryThread = Executors.newSingleThreadScheduledExecutor();
				AtomicLong maxUsedMemory = new AtomicLong(0);

				AtomicLong pid = new AtomicLong(-1);

				memoryThread.scheduleAtFixedRate(() -> {
					if (pid.get() == -1) {
						return;
					}

					try {
						Process process = new ProcessBuilder().command("sh",
																	   "-c",
																	   "ps -o rss= -p %d".formatted(pid.get())
						).start();

						try (
								BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))
						) {
							String line;
							while ((line = reader.readLine()) != null) {
								long memory = Long.parseLong(line.trim());

								if (memory > maxUsedMemory.get()) {
									maxUsedMemory.set(memory);
								}
							}
						} catch (IOException ignored) {
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}, 0, 1, TimeUnit.MILLISECONDS);

				// Run process
				Process process = null;
				try {
					process = new ProcessBuilder().command("sh", "-c", language.getRun()).start();

					pid.set(process.pid());

					// Input
					String input = testCase.getString("input");
					process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
					process.getOutputStream().close();
				} catch (IOException ignored) {
				}

				assert process != null;

				// Runtime Error Check
				StringBuilder sb = new StringBuilder();
				try (
						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))
				) {
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append('\n');
					}
				}
				if (!sb.isEmpty()) {
					sb.setLength(sb.length() - 1);
				}
				if (!sb.isEmpty()) {
					log.error("[Judge] Runtime Error - %s".formatted(sb.toString()));
					setResult(JudgeResult.RUNTIME_ERROR, Map.of("message", sb.toString()));
					memoryThread.shutdown();
					return Optional.of(new JudgeRuntimeException());
				}

				// Output Check
				sb = new StringBuilder();
				try (
						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))
				) {
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append('\n');
					}
				}
				if (!sb.isEmpty()) {
					sb.setLength(sb.length() - 1);
				}

				// Memory check
				long memory = maxUsedMemory.get();
				sumMemory.getAndAdd(memory);
				if (memory > problem.getLong("memoryLimit") * 1024) {
					log.info("[Judge] Memory limit exceeded. Max: %d".formatted(problem.getLong("memoryLimit")));
					setResult(JudgeResult.MEMORY_LIMIT_EXCEEDED, Map.of("input", testCase.getString("input")));
					memoryThread.shutdown();
					return Optional.of(new MemoryLimitException());
				}

				// Compare output with expected output
				String output = testCase.getString("output");
				if (!sb.toString().equals(output)) {
					log.info("[Judge] Test case fail. Expected: %s, Actual: %s".formatted(output, sb.toString()));
					setResult(JudgeResult.WRONG_ANSWER, Map.of("expected", output, "actual", sb.toString()));
					memoryThread.shutdown();
					return Optional.of(new WrongAnswerException());
				}

				memoryThread.shutdown();

				return Optional.empty();
			});

			Optional<? extends Exception> exception;

			// Time limit check
			try {
				long before = System.currentTimeMillis();
				exception = judgeTask.get(problem.getLong("timeLimit"), TimeUnit.MILLISECONDS);
				long after = System.currentTimeMillis();

				sumTime.getAndAdd(after - before);
			} catch (TimeoutException e) {
				log.info("[Judge] Time limit exceeded for test case");
				setResult(JudgeResult.TIME_LIMIT_EXCEEDED, Map.of("input", testCase.getString("input")));

				throw new TimeLimitException();
			} finally {
				if (!judgeTask.isCancelled()) {
					judgeTask.cancel(true);
				}
			}

			if (exception.isPresent()) {
				throw exception.get();
			}
		}

		log.info("[Judge] All test cases passed.");
		setResult(JudgeResult.ACCEPT,
				  Map.of("time", sumTime.get() / testCases.size(), "memory", sumMemory.get() / testCases.size())
		);

		if (!judgeThread.isShutdown()) {
			judgeThread.shutdown();
		}
	}

	private void setResult(JudgeResult result) {

		setResult(result, Map.of());
	}

	private void setResult(JudgeResult result, Map<?, ?> data) {

		mongoBridge.setResult(judgeInfo, result, data);
	}
}
