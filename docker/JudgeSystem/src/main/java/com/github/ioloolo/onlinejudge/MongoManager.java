package com.github.ioloolo.onlinejudge;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;

public final class MongoManager {

	@Getter
	private static final MongoManager instance = new MongoManager();

	@Getter
	private final MongoCollection<Document> judgeCollection;

	@Getter
	private final MongoCollection<Document> problemCollection;

	private MongoManager() {
		MongoClient client = MongoClients.create(
				"mongodb://%s:%s@%s:%s/?authSource=%s".formatted(
						System.getenv("MONGODB_USERNAME"),
						System.getenv("MONGODB_PASSWORD"),
						System.getenv("MONGODB_HOST"),
						System.getenv("MONGODB_PORT"),
						System.getenv("MONGODB_AUTH_DATABASE")
				)
		);

		MongoDatabase database = client.getDatabase(System.getenv("MONGODB_DATABASE"));

		judgeCollection = database.getCollection("judges");
		problemCollection = database.getCollection("problems");
	}
}
