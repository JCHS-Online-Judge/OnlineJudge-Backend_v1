package com.github.ioloolo.onlinejudge.common.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {

	private String error;

	public ErrorResponse(Throwable e) {
		this.error = e.getMessage();
	}
}
