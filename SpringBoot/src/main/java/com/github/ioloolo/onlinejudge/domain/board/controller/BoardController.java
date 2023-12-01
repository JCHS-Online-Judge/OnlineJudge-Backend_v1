package com.github.ioloolo.onlinejudge.domain.board.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.request.BoardInfoRequest;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.request.CreateBoardRequest;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.request.UpdateBoardRequest;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.response.BoardInfoResponse;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.response.ContentsResponse;
import com.github.ioloolo.onlinejudge.domain.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

	private final BoardService service;

	@PostMapping("/all")
	public ResponseEntity<?> getBoards() {

		return ResponseEntity.ok(new ContentsResponse(service.getBoards()));
	}

	@PostMapping
	public ResponseEntity<?> getBoardInfo(
			@Validated(OrderChecks.class) @RequestBody BoardInfoRequest request
	) throws Exception {

		return ResponseEntity.ok(new BoardInfoResponse(service.getBoardInfo(request.getBoardId())));
	}

	@PutMapping
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<?> createBoard(
			@Validated(OrderChecks.class) @RequestBody CreateBoardRequest request,
			@AuthenticationPrincipal UserDetailsImpl userDetails
	) {

		String title = request.getTitle();
		String content = request.getContent();
		boolean isNotice = request.isNotice();
		List<String> files = request.getFiles();

		service.createBoard(title, content, isNotice, files, userDetails.toUser());

		return ResponseEntity.ok().build();
	}

	@PatchMapping
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<?> updateBoard(
			@Validated(OrderChecks.class) @RequestBody UpdateBoardRequest request,
			@AuthenticationPrincipal UserDetailsImpl userDetails
	) throws Exception {

		String boardId = request.getBoardId();
		String title = request.getTitle();
		String content = request.getContent();
		boolean isNotice = request.getIsNotice();
		List<String> files = request.getFiles();

		service.updateBoard(boardId, title, content, isNotice, files, userDetails.toUser());

		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<?> deleteBoard(
			@Validated(OrderChecks.class) @RequestBody BoardInfoRequest request,
			@AuthenticationPrincipal UserDetailsImpl userDetails
	) throws Exception {

		String boardId = request.getBoardId();

		service.deleteBoard(boardId, userDetails.toUser());

		return ResponseEntity.ok().build();
	}
}
