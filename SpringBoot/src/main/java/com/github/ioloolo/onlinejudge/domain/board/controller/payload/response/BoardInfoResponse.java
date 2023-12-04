package com.github.ioloolo.onlinejudge.domain.board.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.board.data.Board;

import java.util.Map;

public class BoardInfoResponse extends KVResponse {

    public BoardInfoResponse(Board board) {

        super(false, Map.of("board", board));
    }
}
