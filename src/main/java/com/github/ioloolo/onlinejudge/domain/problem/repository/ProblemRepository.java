package com.github.ioloolo.onlinejudge.domain.problem.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.ioloolo.onlinejudge.domain.problem.model.Problem;

public interface ProblemRepository extends MongoRepository<Problem, ObjectId> {
	Optional<Problem> findByProblemId(String id);

	boolean existsByProblemId(String id);
	boolean existsByTitle(String title);

	void deleteByProblemId(String id);
}
