package com.github.ioloolo.onlinejudge.domain.judge.data;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.user.data.User;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Document
public class JudgeHistory {

	@Id
	private String id;

	@DBRef
	private User user;

	@DBRef
	private Problem problem;

	private JudgeLanguage language;

	private String sourceCode;

	private LocalDateTime createdTime;

	private JudgeResult result;

	private Map<?, ?> data;
}
