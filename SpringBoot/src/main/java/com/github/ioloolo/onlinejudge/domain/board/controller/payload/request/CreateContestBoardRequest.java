package com.github.ioloolo.onlinejudge.domain.board.controller.payload.request;

import java.util.List;

import com.github.ioloolo.onlinejudge.common.validation.group.NotBlankGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateContestBoardRequest {

	@NotBlank(groups = NotBlankGroup.class, message = "대회 ID는 필수 입력값입니다.")
	private String contestId;

	@NotBlank(groups = NotBlankGroup.class, message = "게시물 제목은 필수 입력값입니다.")
	@Size(min = 5, max = 50, message = "게시물 제목은 5~50자리로 입력해주세요.")
	private String title;

	@NotBlank(groups = NotBlankGroup.class, message = "게시물 내용은 필수 입력값입니다.")
	@Size(min = 10, max = 1000, message = "게시물 내용은 10~1000자리로 입력해주세요.")
	private String content;

	private boolean isNotice;

	private List<String> files;
}
