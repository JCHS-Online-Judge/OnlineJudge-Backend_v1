package com.github.ioloolo.onlinejudge.domain.judge.model;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ioloolo.onlinejudge.domain.auth.model.User;
import com.github.ioloolo.onlinejudge.domain.problem.model.Problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "judges")
public class Judge {

	@Id
	@JsonIgnore
	ObjectId id;

	@DBRef
	User user;

	@DBRef
	Problem problem;

	@NotNull
	Language language;

	@NotEmpty
	String source;

	@NotNull
	LocalDateTime at;

	@NotNull
	Result result;

	public enum Language {
		C, CPP, JAVA, PYTHON
	}

	@Data
	@Builder
	@AllArgsConstructor
	public static class Result {
		Type type;
		long time;
		long memory;
		String message;

		public enum Type {
			WAITING,
			COMPILE_ERROR,
			RUNTIME_ERROR,
			TIME_LIMIT,
			MEMORY_LIMIT,
			WRONG_ANSWER,
			CORRECT
		}
	}
}
