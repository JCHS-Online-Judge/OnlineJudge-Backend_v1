package com.github.ioloolo.onlinejudge.domain.auth.model;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
@Document(collection = "users")
public class User {

  @Id
  @JsonIgnore
  ObjectId id;

  @NotBlank
  @Size(min = 6, max = 16)
  String username;

  @NotBlank
  @JsonIgnore
  @Size(min = 10, max = 32)
  String password;

  @DBRef
  @Singular
  Set<Role> roles;
}
