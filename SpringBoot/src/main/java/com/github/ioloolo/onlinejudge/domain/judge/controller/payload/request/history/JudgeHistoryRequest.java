package com.github.ioloolo.onlinejudge.domain.judge.controller.payload.request.history;

import com.github.ioloolo.onlinejudge.common.validation.group.LengthGroup;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JudgeHistoryRequest {

    @Min(value = 1, groups = LengthGroup.class, message = "페이지 번호는 1 이상이어야 합니다.")
    private int page;
}
