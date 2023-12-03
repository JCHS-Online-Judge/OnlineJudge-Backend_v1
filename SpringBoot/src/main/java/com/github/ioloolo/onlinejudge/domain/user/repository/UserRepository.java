package com.github.ioloolo.onlinejudge.domain.user.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.ioloolo.onlinejudge.domain.user.data.User;

public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByName(String name);
}
