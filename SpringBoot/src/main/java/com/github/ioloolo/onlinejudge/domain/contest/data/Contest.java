package com.github.ioloolo.onlinejudge.domain.contest.data;

import java.time.LocalDateTime;
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
public class Contest {

	@Id
	private String id;

	private String title;
	private String content;

	private String inviteCode;

	private LocalDateTime startTime;
	private LocalDateTime endTime;

	@JsonIgnore
	@Singular
	@DBRef
	private List<User> users;
}
