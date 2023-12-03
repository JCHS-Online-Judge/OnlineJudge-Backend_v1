package com.github.ioloolo.onlinejudge.domain.board.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.board.data.Board;

import java.util.List;
import java.util.Map;

public class BoardListResponse extends KVResponse {

    public BoardListResponse(List<Board.Simple> boards) {

        super(false, Map.of("boards", boards));
    }
}
