package com.github.ioloolo.onlinejudge.domain.board.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.domain.board.data.BoardFile;
import com.github.ioloolo.onlinejudge.domain.board.repository.BoardFileRepository;
import com.github.ioloolo.onlinejudge.domain.user.data.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardFileService {

	private final BoardFileRepository repository;

	@Getter
	private final String uploadDir = "./upload/";

	public String uploadFile(MultipartFile file, User user) throws Exception {

		if (file == null || file.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.FILE_IS_EMPTY);
		}

		String name = file.getOriginalFilename();
		String path = UUID.randomUUID().toString();

		BoardFile file1 = repository.save(BoardFile.builder()
												  .name(name)
												  .path(path)
												  .uploadedTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
												  .uploader(user)
												  .build());

		Path filePath = Paths.get(uploadDir).resolve(path);
		Files.copy(file.getInputStream(), filePath);

		return file1.getId();
	}

	public ResponseEntity<?> downloadFile(String id) throws Exception {

		if (!repository.existsById(id)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.FILE_NOT_FOUND);
		}

		BoardFile file = repository.findById(id).orElseThrow();

		Path filePath = Paths.get(uploadDir).resolve(file.getPath()).normalize();
		Resource resource = new UrlResource(filePath.toUri());

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header("File-Name", file.getName())
				.body(resource);
	}
}
