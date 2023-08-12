package com.github.ioloolo.onlinejudge.domain.judge.model;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.github.ioloolo.onlinejudge.config.security.services.UserDetailsImpl;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class History {

	@NotEmpty
	String username;

	@NotEmpty
	String problemId;

	@NotNull
	Judge.Language language;

	@NotEmpty

	String source;

	int sourceLength;

	@NotNull
	LocalDateTime at;

	@NotNull
	Judge.Result result;

	public static History from(Judge judge, UserDetailsImpl user) {
		Judge.Result result = judge.getResult();
		String source = judge.getSource();

		if (!user.getId().equals(judge.getUser().getId().toString())) {
			result.setMessage(null);
			source = "";
		}

		return History.builder()
				.username(judge.getUser().getUsername())
				.problemId(judge.getProblem().getProblemId())
				.language(judge.getLanguage())
				.source(source)
				.sourceLength(judge.getSource().getBytes().length)
				.at(judge.getAt())
				.result(result)
				.build();
	}
}
