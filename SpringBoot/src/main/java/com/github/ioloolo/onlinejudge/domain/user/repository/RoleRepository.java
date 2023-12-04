package com.github.ioloolo.onlinejudge.domain.user.repository;

import com.github.ioloolo.onlinejudge.domain.user.data.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByRole(Role.Roles role);
}
