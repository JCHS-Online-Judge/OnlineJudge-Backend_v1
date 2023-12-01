package com.github.ioloolo.onlinejudge.common.payload.response;

import java.util.Map;

public class ErrorResponse extends KVResponse {

	public ErrorResponse(Exception exception) {

		this(exception.getMessage());
	}

	public ErrorResponse(String message) {

		super(true, Map.of("error", message));
	}
}
