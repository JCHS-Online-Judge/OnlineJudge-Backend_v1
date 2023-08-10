package com.github.ioloolo.onlinejudge;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.DBRef;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public final class JudgeInfo {

	private static final MongoManager mongoManager = MongoManager.getInstance();
	private static final String judgeId = System.getenv("JUDGE_ID");

	private final String source;
	private final Language language;

	private final long timeLimit;
	private final long memoryLimit;

	private final List<TestCase> testCases;

	public static JudgeInfo getJudgeInfo() {
		Document judge = mongoManager.getJudgeCollection()
				.find(new Document("_id", new ObjectId(judgeId)))
				.first();
		assert judge != null;

		Document problem = mongoManager.getProblemCollection()
				.find(new Document("_id", judge.get("problem", DBRef.class).getId()))
				.first();
		assert problem != null;

		//noinspection unchecked
		return JudgeInfo.builder()
				.source(judge.getString("source"))
				.language(JudgeInfo.Language.valueOf(judge.getString("language")))
				.timeLimit(problem.getLong("timeLimit"))
				.memoryLimit(problem.getLong("memoryLimit"))
				.testCases(JudgeInfo.TestCase.parse(problem.get("testCases", List.class)))
				.build();
	}

	public void updateState(ExceptionType type, Object... args) {
		mongoManager.getJudgeCollection().updateOne(
				new Document("_id", new ObjectId(judgeId)),
				new Document("$set", new Document("result.type", type.toString()))
		);

		switch (type) {
			case WAITING -> mongoManager.getJudgeCollection().updateOne(
					new Document("_id", new ObjectId(judgeId)),
					new Document("$set", new Document(
							Map.of("result.time", -1,
									"result.memory", -1,
									"result.message", "")))
			);
			case COMPILE_ERROR, RUNTIME_ERROR -> mongoManager.getJudgeCollection().updateOne(
					new Document("_id", new ObjectId(judgeId)),
					new Document("$set", new Document("result.message", args[0]))
			);
			case CORRECT -> mongoManager.getJudgeCollection().updateOne(
					new Document("_id", new ObjectId(judgeId)),
					new Document("$set", new Document(
							Map.of("result.time", args[0],
									"result.memory", args[1])))
			);
		}

		if (type != ExceptionType.WAITING) {
			Runtime.getRuntime().exit(0);
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum Language {
		C("c", "gcc Main.c -o Main -O2 -Wall -lm -static -std=gnu11", "./Main"),
		CPP("cpp", "g++ Main.cpp -o Main -O2 -Wall -lm -static -std=gnu++17", "./Main"),
		JAVA("java", "javac -encoding UTF-8 Main.java", "java Main"),
		PYTHON("py", null, "python3 Main.py"),
		;

		private final String extension;
		private final String compile;
		private final String run;
	}

	@Data
	@Builder
	@RequiredArgsConstructor
	public static class TestCase {

		private final String input;
		private final String output;
		private final boolean isExample;

		public static List<TestCase> parse(List<Document> documentList) {
			return new LinkedList<>() {{
				for (Document document : documentList) {
					add(
							TestCase.builder()
									.input(document.getString("input"))
									.output(document.getString("output"))
									.isExample(document.getBoolean("isExample", false))
									.build()
					);
				}
			}};
		}
	}

	public enum ExceptionType {
		WAITING,
		COMPILE_ERROR,
		RUNTIME_ERROR,
		TIME_LIMIT,
		MEMORY_LIMIT,
		WRONG_ANSWER,
		CORRECT
	}
}
