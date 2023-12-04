package com.github.ioloolo.onlinejudge.domain.lecture.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;

import java.util.Map;

public class InviteCodeResponse extends KVResponse {

    public InviteCodeResponse(String code) {

        super(false, Map.of("code", code));
    }
}
