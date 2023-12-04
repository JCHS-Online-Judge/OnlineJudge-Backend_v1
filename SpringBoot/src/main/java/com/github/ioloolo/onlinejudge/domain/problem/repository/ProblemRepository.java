package com.github.ioloolo.onlinejudge.domain.problem.repository;

import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProblemRepository extends MongoRepository<Problem, String> {
    boolean existsByProblemNumberAndLectureAndContest(
            String problemId, Lecture lecture, Contest contest
    );

    boolean existsByTitleAndLectureAndContest(String title, Lecture lecture, Contest contest);
}
