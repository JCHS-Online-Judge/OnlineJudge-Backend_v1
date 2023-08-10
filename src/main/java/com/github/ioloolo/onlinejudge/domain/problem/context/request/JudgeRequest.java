package com.github.ioloolo.onlinejudge.domain.problem.context.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.github.ioloolo.onlinejudge.domain.judge.model.Judge;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JudgeRequest {

	@NotBlank
	private String problemId;

	@NotNull
	private Judge.Language language;

	@NotBlank
	private String source;
}
