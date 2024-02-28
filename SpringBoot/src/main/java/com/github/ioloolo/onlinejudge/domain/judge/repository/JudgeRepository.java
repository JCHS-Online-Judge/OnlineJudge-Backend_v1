package com.github.ioloolo.onlinejudge.domain.judge.repository;

import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeResult;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JudgeRepository extends MongoRepository<JudgeHistory, String> {

    List<JudgeHistory> findAllByUserAndResult(User user, JudgeResult result);

    List<JudgeHistory> findAllByOrderByCreatedTimeAsc();
}
