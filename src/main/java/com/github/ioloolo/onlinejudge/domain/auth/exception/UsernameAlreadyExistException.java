package com.github.ioloolo.onlinejudge.domain.auth.exception;

public class UsernameAlreadyExistException extends Exception {

	public UsernameAlreadyExistException() {
		super("이미 사용중인 아이디입니다.");
	}
}
