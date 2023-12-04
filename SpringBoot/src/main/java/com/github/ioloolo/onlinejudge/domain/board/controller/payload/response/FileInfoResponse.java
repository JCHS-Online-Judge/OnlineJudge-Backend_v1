package com.github.ioloolo.onlinejudge.domain.board.controller.payload.response;

import com.github.ioloolo.onlinejudge.common.payload.response.KVResponse;

import java.util.Map;

public class FileInfoResponse extends KVResponse {

    public FileInfoResponse(String fileName) {

        super(false, Map.of("file", fileName));
    }
}
