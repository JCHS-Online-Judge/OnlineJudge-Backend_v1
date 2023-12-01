package com.github.ioloolo.onlinejudge.domain.lecture.data;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ioloolo.onlinejudge.domain.user.data.User;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Document
public class Lecture {

	@Id
	private String id;

	private String title;
	private String content;

	private String inviteCode;

	@JsonIgnore
	@Singular
	@DBRef
	private List<User> users;
}
