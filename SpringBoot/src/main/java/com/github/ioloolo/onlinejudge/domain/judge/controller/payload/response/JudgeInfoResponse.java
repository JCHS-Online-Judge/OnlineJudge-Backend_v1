package com.github.ioloolo.onlinejudge.domain.judge.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;

import java.util.Map;

public class JudgeInfoResponse extends KVResponse {

    public JudgeInfoResponse(JudgeHistory info) {

        super(false, Map.of("info", info));
    }
}
