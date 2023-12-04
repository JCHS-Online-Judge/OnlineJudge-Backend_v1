package com.github.ioloolo.onlinejudge.domain.problem.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;

import java.util.Map;

public class ProblemInfoResponse extends KVResponse {

    public ProblemInfoResponse(Problem problem) {

        super(false, Map.of("problem", problem));
    }
}
