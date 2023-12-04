package com.github.ioloolo.onlinejudge.domain.user.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;

import java.util.Map;

public class TokenResponse extends KVResponse {

    public TokenResponse(String token) {

        super(Map.of("token", "Bearer " + token));
    }
}
