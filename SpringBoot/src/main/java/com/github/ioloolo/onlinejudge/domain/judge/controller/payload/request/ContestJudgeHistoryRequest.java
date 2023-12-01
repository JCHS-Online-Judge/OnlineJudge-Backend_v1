package com.github.ioloolo.onlinejudge.domain.judge.controller.payload.request;

import com.github.ioloolo.onlinejudge.common.validation.group.LengthGroup;
import com.github.ioloolo.onlinejudge.common.validation.group.NotBlankGroup;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContestJudgeHistoryRequest {

	@NotBlank(groups = NotBlankGroup.class, message = "대회 ID는 필수 입력값입니다.")
	private String contestId;

	@Min(value = 1, groups = LengthGroup.class, message = "페이지 번호는 1 이상이어야 합니다.")
	private int page;
}
