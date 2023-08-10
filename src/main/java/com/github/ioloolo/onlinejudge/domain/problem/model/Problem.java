package com.github.ioloolo.onlinejudge.domain.problem.model;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
@Document(collection = "problems")
public class Problem {

	@Id
	@JsonIgnore
	ObjectId id;

	@NotBlank
	String problemId;

	@NotBlank
	@Length(min = 4, max = 20)
	String title;

	@NotBlank
	@Length(min = 10, max = 500)
	String description;

	@NotBlank
	@Length(max = 500)
	String inputDescription;

	@NotBlank
	@Length(max = 500)
	String outputDescription;

	@Min(1000)
	@Max(10000)
	long timeLimit;

	@Min(128)
	@Max(1024)
	long memoryLimit;

	@Singular
	@NotEmpty
	List<TestCase> testCases;

	@Data
	@Builder
	@AllArgsConstructor
	public static class TestCase {
		String input;
		String output;
		boolean isExample;
	}
}
