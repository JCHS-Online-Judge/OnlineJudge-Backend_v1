package com.github.ioloolo.onlinejudge.domain.contest.controller.payload.request;

import com.github.ioloolo.onlinejudge.common.validation.group.NotBlankGroup;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefreshInviteCodeRequest {

	@NotBlank(groups = NotBlankGroup.class, message = "대회 ID는 필수 입력값입니다.")
	private String contestId;
}
