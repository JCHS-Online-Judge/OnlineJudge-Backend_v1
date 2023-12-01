package com.github.ioloolo.onlinejudge.domain.contest.controller.payload.response;

import java.util.Map;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;

public class InviteCodeResponse extends KVResponse {

	public InviteCodeResponse(String code) {

		super(false, Map.of("code", code));
	}
}
