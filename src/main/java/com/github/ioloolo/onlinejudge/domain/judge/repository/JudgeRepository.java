package com.github.ioloolo.onlinejudge.domain.judge.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.github.ioloolo.onlinejudge.domain.judge.model.Judge;

@Repository
public interface JudgeRepository extends MongoRepository<Judge, ObjectId> {
}
