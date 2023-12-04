package com.github.ioloolo.onlinejudge.domain.problem.repository;

import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProblemRepository extends MongoRepository<Problem, String> {
    boolean existsByProblemNumberAndContest(String problemId, Contest contest);

    boolean existsByTitleAndContest(String title, Contest contest);
}
