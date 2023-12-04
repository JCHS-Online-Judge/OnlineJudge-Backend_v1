package com.github.ioloolo.onlinejudge.domain.user.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Document
public class User {

    @Id
    private String id;

    private String username;

    @JsonIgnore
    private String password;

    private String name;

    @DBRef
    @Singular
    private List<Role> roles;

    public boolean isAdmin() {

        return roles.stream().map(Role::getRole).anyMatch(role -> role.equals(Role.Roles.ROLE_ADMIN));
    }
}
