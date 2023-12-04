package com.github.ioloolo.onlinejudge.judge.container.util;

import com.github.ioloolo.onlinejudge.judge.container.data.JudgeResult;
import com.mongodb.DBRef;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE, staticName = "of")
public class MongoBridge {

    private static final ProcessEnvironment.MongoConnectionInfo mongoConnectionInfo = ProcessEnvironment.getInstance()
            .getMongoConnectionInfo();

    private MongoDatabase mongoDatabase;

    private MongoCollection<Document> judgeCollection;

    public static MongoBridge getInstance() {

        return LazyHolder.INSTANCE;
    }

    public void init() {

        MongoClient client = MongoClients.create("mongodb://%s:%s@%s:%s/?authSource=%s".formatted(
                mongoConnectionInfo.getUsername(),
                mongoConnectionInfo.getPassword(),
                mongoConnectionInfo.getHost(),
                mongoConnectionInfo.getPort(),
                mongoConnectionInfo.getAuthDatabase()
        ));

        mongoDatabase = client.getDatabase(mongoConnectionInfo.getDatabase());

        judgeCollection = mongoDatabase.getCollection(mongoConnectionInfo.getJudgeCollection());
    }

    public Document findById(String id) {

        return judgeCollection.find(eq("_id", new ObjectId(id))).first();
    }

    public void setResult(Document document, JudgeResult result, Map<?, ?> data) {

        judgeCollection.updateOne(
                eq("_id", document.get("_id", ObjectId.class)),
                new Document("$set", new Document("result", result.name()).append("data", data))
        );
    }

    public Document reference(DBRef ref) {

        return mongoDatabase.getCollection(ref.getCollectionName())
                .find(eq("_id", ref.getId()))
                .first();
    }

    private static class LazyHolder {

        private static final MongoBridge INSTANCE = MongoBridge.of();
    }
}
