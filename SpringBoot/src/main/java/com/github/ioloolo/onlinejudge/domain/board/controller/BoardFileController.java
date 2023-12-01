package com.github.ioloolo.onlinejudge.domain.board.controller;

import java.io.File;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.request.FileDownloadRequest;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.response.FileInfoResponse;
import com.github.ioloolo.onlinejudge.domain.board.service.BoardFileService;
import com.github.ioloolo.onlinejudge.domain.user.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/file")
public class BoardFileController {

	private final BoardFileService service;
	private final UserRepository   userRepository;

	@PutMapping
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<?> uploadFile(
			@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetailsImpl userDetails
	) throws Exception {

		String fileName = service.uploadFile(file, userDetails.toUser());

		return ResponseEntity.ok(new FileInfoResponse(fileName));
	}

	@PostMapping
	public ResponseEntity<?> downloadFile(
			@Validated(OrderChecks.class) @RequestBody FileDownloadRequest request
	) throws Exception {

		String fileId = request.getFileId();

		return service.downloadFile(fileId);
	}

	@PostConstruct
	public void init() {

		File folder = new File(service.getUploadDir());

		if (!folder.exists()) {
			//noinspection ResultOfMethodCallIgnored
			folder.mkdirs();
		}
	}
}
