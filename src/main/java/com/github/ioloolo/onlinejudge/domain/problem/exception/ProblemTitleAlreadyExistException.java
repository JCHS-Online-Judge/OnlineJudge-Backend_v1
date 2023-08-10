package com.github.ioloolo.onlinejudge.domain.problem.exception;

import com.github.ioloolo.onlinejudge.domain.problem.model.Problem;

public class ProblemTitleAlreadyExistException extends Exception {

	public ProblemTitleAlreadyExistException(Problem problem) {
		super("문제 이름(%s)는 이미 사용중입니다.".formatted(problem.getTitle()));
	}
}
