package com.github.ioloolo.onlinejudge.domain.lecture.controller;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.lecture.controller.payload.request.*;
import com.github.ioloolo.onlinejudge.domain.lecture.controller.payload.response.InviteCodeResponse;
import com.github.ioloolo.onlinejudge.domain.lecture.controller.payload.response.LectureInfoResponse;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import com.github.ioloolo.onlinejudge.domain.lecture.service.LectureService;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lecture")
public class LectureController {

    private final LectureService service;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<LectureInfoResponse> getLectureInfo(
            @Validated(OrderChecks.class) @RequestBody LectureInfoRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String lectureId = request.getLectureId();
        User user = userDetails.toUser();

        Lecture lectureInfo = service.getLectureInfo(lectureId, user);

        return ResponseEntity.ok(new LectureInfoResponse(lectureInfo));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> createLecture(
            @Validated(OrderChecks.class) @RequestBody CreateLectureRequest request
    ) throws Exception {

        String title = request.getTitle();
        String content = request.getContent();

        service.createLecture(title, content);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/inviteCode")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<InviteCodeResponse> refreshInviteCode(
            @Validated(OrderChecks.class) @RequestBody RefreshInviteCodeRequest request
    ) throws Exception {

        String lectureId = request.getLectureId();
        String inviteCode = service.refreshInviteCode(lectureId);

        return ResponseEntity.ok(new InviteCodeResponse(inviteCode));
    }

    @PatchMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateLecture(
            @Validated(OrderChecks.class) @RequestBody UpdateLectureRequest request
    ) throws Exception {

        String lectureId = request.getLectureId();

        String title = request.getTitle();
        String content = request.getContent();

        service.updateLecture(lectureId, title, content);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteLecture(
            @Validated(OrderChecks.class) @RequestBody DeleteLectureRequest request
    ) throws Exception {

        String lectureId = request.getLectureId();

        service.deleteLecture(lectureId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/participate")
    public ResponseEntity<Void> joinLecture(
            @Validated(OrderChecks.class) @RequestBody JoinLectureRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String code = request.getCode();

        service.joinLecture(code, userDetails.toUser());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/participate")
    public ResponseEntity<Void> leaveLecture(
            @Validated(OrderChecks.class) @RequestBody LeaveLectureRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String lectureId = request.getLectureId();

        service.leaveLecture(lectureId, userDetails.toUser());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/participate/force")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> forceJoinLecture(
            @Validated(OrderChecks.class) @RequestBody ForceJoinLectureRequest request
    ) throws Exception {

        String lectureId = request.getLectureId();
        String userId = request.getUserId();

        service.forceJoinLecture(lectureId, userId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/participate/force")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> forceLeaveLecture(
            @Validated(OrderChecks.class) @RequestBody ForceLeaveLectureRequest request
    ) throws Exception {

        String lectureId = request.getLectureId();
        String userId = request.getUserId();

        service.forceLeaveLecture(lectureId, userId);

        return ResponseEntity.ok().build();
    }
}
