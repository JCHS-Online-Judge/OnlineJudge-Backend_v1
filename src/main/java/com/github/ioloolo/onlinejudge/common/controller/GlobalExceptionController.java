package com.github.ioloolo.onlinejudge.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.Data;

@ControllerAdvice
public class GlobalExceptionController {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(Exception e) {
		if (e instanceof Errors err && err.getFieldError() != null) {
			FieldError fieldError = err.getFieldErrors().get(0);
			e = new Exception(fieldError.getField() + "은/는 " + fieldError.getDefaultMessage());
		}

		return ResponseEntity.internalServerError().body(new ErrorResponse(e));
	}

	@Data
	public static class ErrorResponse {
		private final String error;

		public ErrorResponse(Throwable e) {
			this.error = e.getMessage();
		}
	}
}
