package com.github.ioloolo.onlinejudge.domain.board.controller.payload.response;

import java.util.List;
import java.util.Map;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;
import com.github.ioloolo.onlinejudge.domain.board.data.Board;

public class ContentsResponse extends KVResponse {

	public ContentsResponse(List<Board.Simple> boards) {

		super(false, Map.of("boards", boards));
	}
}
