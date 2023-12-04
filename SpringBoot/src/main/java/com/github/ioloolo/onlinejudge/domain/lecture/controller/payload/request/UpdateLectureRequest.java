package com.github.ioloolo.onlinejudge.domain.lecture.controller.payload.request;

import com.github.ioloolo.onlinejudge.common.validation.group.NotBlankGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateLectureRequest {

    @NotBlank(groups = NotBlankGroup.class, message = "수업 ID는 필수 입력값입니다.")
    private String lectureId;

    @NotBlank(groups = NotBlankGroup.class, message = "대회 제목은 필수 입력값입니다.")
    @Size(min = 5, max = 50, message = "대회 제목은 5~50자리로 입력해주세요.")
    private String title;

    @NotBlank(groups = NotBlankGroup.class, message = "대회 내용은 필수 입력값입니다.")
    @Size(min = 10, max = 1000, message = "대회 내용은 10~1000자리로 입력해주세요.")
    private String content;
}
