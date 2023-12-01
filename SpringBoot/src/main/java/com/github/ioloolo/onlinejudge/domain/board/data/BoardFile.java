package com.github.ioloolo.onlinejudge.domain.board.data;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.github.ioloolo.onlinejudge.domain.user.data.User;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Document
public class BoardFile {

	@Id
	private String id;

	private String name;
	private String path;

	private LocalDateTime uploadedTime;

	@DBRef
	private User uploader;
}
