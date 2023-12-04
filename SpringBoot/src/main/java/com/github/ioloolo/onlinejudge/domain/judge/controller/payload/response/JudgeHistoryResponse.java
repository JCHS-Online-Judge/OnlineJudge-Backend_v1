package com.github.ioloolo.onlinejudge.domain.judge.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;

import java.util.List;
import java.util.Map;

public class JudgeHistoryResponse extends KVResponse {

    public JudgeHistoryResponse(List<JudgeHistory> history, int maxPage) {

        super(false, Map.of("history", history, "maxPage", maxPage));
    }
}
