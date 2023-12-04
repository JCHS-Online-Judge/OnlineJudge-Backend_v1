package com.github.ioloolo.onlinejudge.domain.user.repository;

import com.github.ioloolo.onlinejudge.domain.user.data.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByName(String name);
}
