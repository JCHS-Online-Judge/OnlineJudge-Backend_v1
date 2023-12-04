package com.github.ioloolo.onlinejudge.domain.lecture.controller.payload.request;

import com.github.ioloolo.onlinejudge.common.validation.group.NotBlankGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JoinLectureRequest {

    @NotBlank(groups = NotBlankGroup.class, message = "초대 코드는 필수 입력값입니다.")
    private String code;
}
