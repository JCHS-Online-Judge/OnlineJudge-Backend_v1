package com.github.ioloolo.onlinejudge.domain.auth.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.ioloolo.onlinejudge.domain.auth.model.Role;

public interface RoleRepository extends MongoRepository<Role, ObjectId> {

  Optional<Role> findByName(Role.Roles name);
}
