package com.github.ioloolo.onlinejudge.domain.auth.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "roles")
public class Role {

  @Id
  @JsonIgnore
  ObjectId id;

  Roles name;

  public enum Roles {
    ROLE_USER,
    ROLE_ADMIN
  }
}
