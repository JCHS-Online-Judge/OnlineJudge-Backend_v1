package com.github.ioloolo.onlinejudge.domain.problem.exception;

import com.github.ioloolo.onlinejudge.domain.problem.model.Problem;

public class ProblemIdAlreadyExistException extends Exception {

	public ProblemIdAlreadyExistException(Problem problem) {
		super("문제 번호(%s)는 이미 사용중입니다.".formatted(problem.getProblemId()));
	}
}
