package com.github.ioloolo.onlinejudge.domain.judge.controller.payload.request;

import com.github.ioloolo.onlinejudge.common.validation.group.NotBlankGroup;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JudgeRequest {

    @NotBlank(groups = NotBlankGroup.class, message = "문제 ID는 필수 입력값입니다.")
    private String problemId;

    @NotNull(message = "언어는 필수 입력값입니다.")
    private JudgeLanguage language;

    @NotBlank(groups = NotBlankGroup.class, message = "소스코드는 필수 입력값입니다.")
    private String sourceCode;
}
