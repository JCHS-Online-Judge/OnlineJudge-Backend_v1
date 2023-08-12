package com.github.ioloolo.onlinejudge.domain.auth.context.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 6, max = 24)
    private String username;

    @NotBlank
    @Size(min = 8, max = 32)
    private String password;
}
