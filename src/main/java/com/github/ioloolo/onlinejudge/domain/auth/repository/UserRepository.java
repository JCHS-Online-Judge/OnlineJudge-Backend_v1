package com.github.ioloolo.onlinejudge.domain.auth.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.ioloolo.onlinejudge.domain.auth.model.User;

public interface UserRepository extends MongoRepository<User, ObjectId> {

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
