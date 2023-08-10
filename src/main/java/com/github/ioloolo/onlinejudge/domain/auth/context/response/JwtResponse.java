package com.github.ioloolo.onlinejudge.domain.auth.context.response;

import lombok.Getter;

@Getter
public class JwtResponse {
	private final String token;

	public JwtResponse(String accessToken) {
		this.token = "Bearer " + accessToken;
	}
}
