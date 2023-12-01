package com.github.ioloolo.onlinejudge.domain.judge.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;

public interface JudgeRepository extends MongoRepository<JudgeHistory, String> {
}
