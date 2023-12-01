package com.github.ioloolo.onlinejudge.domain.user.controller.payload.request;

import com.github.ioloolo.onlinejudge.common.validation.group.NotBlankGroup;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {

	@NotBlank(message = "아이디는 필수 입력값입니다.", groups = NotBlankGroup.class)
	private final String username;

	@NotBlank(message = "비밀번호는 필수 입력값입니다.", groups = NotBlankGroup.class)
	private final String password;
}
