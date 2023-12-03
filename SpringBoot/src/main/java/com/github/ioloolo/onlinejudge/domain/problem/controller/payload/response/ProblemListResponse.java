package com.github.ioloolo.onlinejudge.domain.problem.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;

import java.util.List;
import java.util.Map;

public class ProblemListResponse extends KVResponse {

    public ProblemListResponse(List<Problem.Simple> problems) {

        super(false, Map.of("problems", problems));
    }
}
