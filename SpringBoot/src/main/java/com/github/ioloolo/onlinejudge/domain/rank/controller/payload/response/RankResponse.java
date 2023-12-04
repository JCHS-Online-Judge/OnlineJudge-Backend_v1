package com.github.ioloolo.onlinejudge.domain.rank.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.rank.data.Rank;

import java.util.List;
import java.util.Map;

public class RankResponse extends KVResponse {

    public RankResponse(List<Rank> rank) {

        super(false, Map.of("rank", rank));
    }
}
