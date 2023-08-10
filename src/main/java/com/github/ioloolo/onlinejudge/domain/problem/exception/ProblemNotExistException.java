package com.github.ioloolo.onlinejudge.domain.problem.exception;

public class ProblemNotExistException extends Exception {

	public ProblemNotExistException(String id) {
		super("%s번 문제는 존재하지 않습니다.".formatted(id));
	}
}
