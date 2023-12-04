package com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request;

import com.github.ioloolo.onlinejudge.common.validation.group.MinMaxGroup;
import com.github.ioloolo.onlinejudge.common.validation.group.NotBlankGroup;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CreateContestProblemRequest {

    @NotBlank(groups = NotBlankGroup.class, message = "대회 ID는 필수 입력값입니다.")
    private String contestId;

    @NotBlank(groups = NotBlankGroup.class, message = "문제 번호는 필수 입력값입니다.")
    @Size(min = 4, max = 5, message = "문제 번호는 4~5자리로 입력해주세요.")
    private String problemNumber;

    @NotBlank(groups = NotBlankGroup.class, message = "문제 이름은 필수 입력값입니다.")
    @Size(min = 5, max = 50, message = "문제 이름은 5~50자리로 입력해주세요.")
    private String title;

    @NotBlank(groups = NotBlankGroup.class, message = "문제 설명은 필수 입력값입니다.")
    @Size(min = 10, max = 1000, message = "문제 설명은 10~1000자리로 입력해주세요.")
    private String description;

    @NotBlank(groups = NotBlankGroup.class, message = "입력 설명은 필수 입력값입니다.")
    @Size(min = 10, max = 1000, message = "입력 설명은 10~1000자리로 입력해주세요.")
    private String inputDescription;

    @NotBlank(groups = NotBlankGroup.class, message = "출력 설명은 필수 입력값입니다.")
    @Size(min = 10, max = 1000, message = "출력 설명은 10~1000자리로 입력해주세요.")
    private String outputDescription;

    @Min(value = 100, groups = MinMaxGroup.class, message = "제한 시간은 0.1초 이상으로 입력해주세요.")
    @Max(value = 10000, groups = MinMaxGroup.class, message = "제한 시간은 10초 이하로 입력해주세요.")
    private long timeLimit;

    @Min(value = 128, groups = MinMaxGroup.class, message = "제한 메모리는 128MB 이상으로 입력해주세요.")
    @Max(value = 1024, groups = MinMaxGroup.class, message = "제한 메모리는 1024MB 이하로 입력해주세요.")
    private long memoryLimit;

    @Size(min = 1, message = "테스트 케이스는 1개 이상 입력해주세요.")
    private List<Problem.TestCase> testCases;
}
