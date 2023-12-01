package com.github.ioloolo.onlinejudge.domain.problem.controller.payload.response;

import java.util.List;
import java.util.Map;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;

public class ProblemsResponse extends KVResponse {

	public ProblemsResponse(List<Problem.Simple> problems) {

		super(false, Map.of("problems", problems));
	}
}
