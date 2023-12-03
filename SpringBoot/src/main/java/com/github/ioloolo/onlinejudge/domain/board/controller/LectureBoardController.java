package com.github.ioloolo.onlinejudge.domain.board.controller;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.request.BoardInfoRequest;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.request.CreateLectureBoardRequest;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.request.LectureBoardsRequest;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.request.UpdateBoardRequest;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.response.BoardInfoResponse;
import com.github.ioloolo.onlinejudge.domain.board.controller.payload.response.BoardListResponse;
import com.github.ioloolo.onlinejudge.domain.board.service.BoardLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/lecture")
public class LectureBoardController {

    private final BoardLectureService service;

    @PostMapping("/all")
    public ResponseEntity<BoardListResponse> getBoards(
            @Validated(OrderChecks.class) @RequestBody LectureBoardsRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String lectureId = request.getLectureId();

        return ResponseEntity.ok(new BoardListResponse(service.getBoards(lectureId, userDetails.toUser())));
    }

    @PostMapping
    public ResponseEntity<BoardInfoResponse> getBoardInfo(
            @Validated(OrderChecks.class) @RequestBody BoardInfoRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String boardId = request.getBoardId();

        return ResponseEntity.ok(new BoardInfoResponse(service.getBoardInfo(boardId, userDetails.toUser())));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> createBoard(
            @Validated(OrderChecks.class) @RequestBody CreateLectureBoardRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String lectureId = request.getLectureId();

        String title = request.getTitle();
        String content = request.getContent();
        boolean isNotice = request.isNotice();
        List<String> files = request.getFiles();

        service.createBoard(lectureId, title, content, isNotice, files, userDetails.toUser());

        return ResponseEntity.ok().build();
    }

    @PatchMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateBoard(
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
    public ResponseEntity<Void> deleteBoard(
            @Validated(OrderChecks.class) @RequestBody BoardInfoRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String boardId = request.getBoardId();

        service.deleteBoard(boardId, userDetails.toUser());

        return ResponseEntity.ok().build();
    }
}
