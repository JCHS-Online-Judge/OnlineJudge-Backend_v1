package com.github.ioloolo.onlinejudge.domain.user.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.ioloolo.onlinejudge.domain.user.data.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
	Optional<Role> findByRole(Role.Roles role);
}
