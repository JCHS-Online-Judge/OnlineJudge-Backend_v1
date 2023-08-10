package com.github.ioloolo.onlinejudge.domain.auth.model;

import java.util.Set;

import javax.validation.constraints.Email;
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
  @Size(max = 20)
  String username;

  @NotBlank
  @Size(max = 50)
  @Email
  String email;

  @NotBlank
  @JsonIgnore
  @Size(max = 120)
  String password;

  @DBRef
  @Singular
  Set<Role> roles;
}
