package com.github.ioloolo.onlinejudge.judge.container.util;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE, staticName = "of")
public class ProcessEnvironment {

	private final MongoConnectionInfo mongoConnectionInfo = new MongoConnectionInfo();

	private final ArgumentInfo argumentInfo = new ArgumentInfo();

	public static ProcessEnvironment getInstance() {

		return LazyHolder.INSTANCE;
	}

	public void init() {

		initMongoConnectionInfo();
		initArgumentInfo();
	}

	private void initMongoConnectionInfo() {

		mongoConnectionInfo.setUsername(get("MONGODB_USERNAME"));
		mongoConnectionInfo.setPassword(get("MONGODB_PASSWORD"));
		mongoConnectionInfo.setHost(get("MONGODB_HOST"));
		mongoConnectionInfo.setPort(get("MONGODB_PORT"));
		mongoConnectionInfo.setAuthDatabase(get("MONGODB_AUTH_DATABASE"));
		mongoConnectionInfo.setDatabase(get("MONGODB_DATABASE"));
		mongoConnectionInfo.setJudgeCollection(get("MONGODB_JUDGE_COLLECTION"));
	}

	private void initArgumentInfo() {

		argumentInfo.setJudgeId(get("JUDGE_ID"));
	}

	private String get(String key) {

		String value = System.getenv(key);

		if (value == null) {
			throw new RuntimeException("Environment variable %s is not set.".formatted(key));
		}

		return value;
	}

	@Data
	public static class MongoConnectionInfo {
		private String username;
		private String password;
		private String host;
		private String port;
		private String authDatabase;
		private String database;
		private String judgeCollection;
	}

	@Data
	public static class ArgumentInfo {
		private String judgeId;
	}

	private static class LazyHolder {
		private static final ProcessEnvironment INSTANCE = ProcessEnvironment.of();
	}
}
