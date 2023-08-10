package com.github.ioloolo.onlinejudge.domain.auth.context.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {

	@NotBlank
	private final String username;

	@NotBlank
	private final String password;
}
