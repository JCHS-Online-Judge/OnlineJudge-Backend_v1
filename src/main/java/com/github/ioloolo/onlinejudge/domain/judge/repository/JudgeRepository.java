package com.github.ioloolo.onlinejudge.domain.judge.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.github.ioloolo.onlinejudge.domain.judge.model.Judge;
import com.github.ioloolo.onlinejudge.domain.problem.model.Problem;

@Repository
public interface JudgeRepository extends MongoRepository<Judge, ObjectId> {
	List<Judge> findAllByProblem(Problem problem);
}
