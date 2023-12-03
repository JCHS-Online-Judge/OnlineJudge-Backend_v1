package com.github.ioloolo.onlinejudge.domain.rank.controller.payload.request;

import com.github.ioloolo.onlinejudge.common.validation.group.NotBlankGroup;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ForceLeaveLectureRequest {

	@NotBlank(groups = NotBlankGroup.class, message = "수업 ID는 필수 입력값입니다.")
	private String lectureId;

	@NotBlank(groups = NotBlankGroup.class, message = "사용자 ID는 필수 입력값입니다.")
	private String userId;
}
