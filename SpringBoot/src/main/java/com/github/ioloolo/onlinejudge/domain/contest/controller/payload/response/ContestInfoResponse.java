package com.github.ioloolo.onlinejudge.domain.contest.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;

import java.util.Map;

public class ContestInfoResponse extends KVResponse {

    public ContestInfoResponse(Contest contest) {

        super(false, Map.of("contest", contest));
    }
}
