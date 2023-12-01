package com.github.ioloolo.onlinejudge.domain.user.controller.payload.response;

import java.util.Map;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;

public class TokenResponse extends KVResponse {

	public TokenResponse(String token) {

		super(Map.of("token", "Bearer " + token));
	}
}
