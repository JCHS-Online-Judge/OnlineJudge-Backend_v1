package com.github.ioloolo.onlinejudge.domain.auth.exception;

public class EmailAlreadyExistException extends Exception {

	public EmailAlreadyExistException() {
		super("이미 사용중인 이메일입니다.");
	}
}
