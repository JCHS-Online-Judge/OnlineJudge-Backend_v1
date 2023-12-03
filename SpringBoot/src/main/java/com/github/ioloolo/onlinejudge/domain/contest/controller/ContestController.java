package com.github.ioloolo.onlinejudge.domain.contest.controller;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.contest.controller.payload.request.*;
import com.github.ioloolo.onlinejudge.domain.contest.controller.payload.response.ContestInfoResponse;
import com.github.ioloolo.onlinejudge.domain.contest.controller.payload.response.InviteCodeResponse;
import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import com.github.ioloolo.onlinejudge.domain.contest.service.ContestService;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contest")
public class ContestController {

    private final ContestService service;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ContestInfoResponse> getContestInfo(
            @Validated(OrderChecks.class) @RequestBody ContestInfoRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String contestId = request.getContestId();
        User user = userDetails.toUser();

        Contest contestInfo = service.getContestInfo(contestId, user);

        return ResponseEntity.ok(new ContestInfoResponse(contestInfo));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> createContest(
            @Validated(OrderChecks.class) @RequestBody CreateContestRequest request
    ) throws Exception {

        String title = request.getTitle();
        String content = request.getContent();
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        service.createContest(title, content, startTime, endTime);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/inviteCode")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<InviteCodeResponse> refreshInviteCode(
            @Validated(OrderChecks.class) @RequestBody RefreshInviteCodeRequest request
    ) throws Exception {

        String contestId = request.getContestId();
        String inviteCode = service.refreshInviteCode(contestId);

        return ResponseEntity.ok(new InviteCodeResponse(inviteCode));
    }

    @PatchMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateContest(
            @Validated(OrderChecks.class) @RequestBody UpdateContestRequest request
    ) throws Exception {

        String contestId = request.getContestId();

        String title = request.getTitle();
        String content = request.getContent();
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        service.updateContest(contestId, title, content, startTime, endTime);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteContest(
            @Validated(OrderChecks.class) @RequestBody DeleteContestRequest request
    ) throws Exception {

        String contestId = request.getContestId();

        service.deleteContest(contestId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/participate")
    public ResponseEntity<Void> joinContest(
            @Validated(OrderChecks.class) @RequestBody JoinContestRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String code = request.getCode();

        service.joinContest(code, userDetails.toUser());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/participate")
    public ResponseEntity<Void> leaveContest(
            @Validated(OrderChecks.class) @RequestBody LeaveContestRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String contestId = request.getContestId();

        service.leaveContest(contestId, userDetails.toUser());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/participate/force")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> forceJoinContest(
            @Validated(OrderChecks.class) @RequestBody ForceJoinContestRequest request
    ) throws Exception {

        String contestId = request.getContestId();
        String userId = request.getUserId();

        service.forceJoinContest(contestId, userId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/participate/force")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> forceLeaveContest(
            @Validated(OrderChecks.class) @RequestBody ForceLeaveContestRequest request
    ) throws Exception {

        String contestId = request.getContestId();
        String userId = request.getUserId();

        service.forceLeaveContest(contestId, userId);

        return ResponseEntity.ok().build();
    }
}
