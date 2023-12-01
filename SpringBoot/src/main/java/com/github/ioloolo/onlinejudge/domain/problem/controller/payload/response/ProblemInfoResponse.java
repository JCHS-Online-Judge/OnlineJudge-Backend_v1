package com.github.ioloolo.onlinejudge.domain.problem.controller.payload.response;

import java.util.Map;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;

public class ProblemInfoResponse extends KVResponse {

	public ProblemInfoResponse(Problem problem) {

		super(false, Map.of("problem", problem));
	}
}
