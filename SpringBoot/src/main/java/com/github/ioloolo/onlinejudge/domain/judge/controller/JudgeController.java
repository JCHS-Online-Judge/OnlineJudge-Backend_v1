package com.github.ioloolo.onlinejudge.domain.judge.controller;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.judge.controller.payload.request.*;
import com.github.ioloolo.onlinejudge.domain.judge.controller.payload.response.JudgeHistoryResponse;
import com.github.ioloolo.onlinejudge.domain.judge.controller.payload.response.JudgeInfoResponse;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeLanguage;
import com.github.ioloolo.onlinejudge.domain.judge.service.JudgeService;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/judge")
public class JudgeController {

    private final JudgeService service;

    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<JudgeInfoResponse> judge(
            @Validated(OrderChecks.class) @RequestBody JudgeRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String problemId = request.getProblemId();
        JudgeLanguage language = request.getLanguage();
        String sourceCode = request.getSourceCode();

        JudgeHistory history = service.judge(problemId, language, sourceCode, userDetails.toUser());

        return ResponseEntity.ok(new JudgeInfoResponse(history));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<JudgeInfoResponse> getInfo(
            @Validated(OrderChecks.class) @RequestBody JudgeInfoRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String judgeId = request.getJudgeId();

        JudgeHistory info = service.getInfo(judgeId, userDetails.toUser());

        return ResponseEntity.ok(new JudgeInfoResponse(info));
    }

    @PostMapping("/history")
    public ResponseEntity<JudgeHistoryResponse> getHistory(
            @Validated(OrderChecks.class) @RequestBody JudgeHistoryRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        int page = request.getPage();

        List<JudgeHistory> history = service.getHistory(page, userDetails == null ? null : userDetails.toUser());
        int maxPage = service.getHistoryMaxPage();

        return ResponseEntity.ok(new JudgeHistoryResponse(history, maxPage));
    }

    @PostMapping("/history/lecture")
    public ResponseEntity<JudgeHistoryResponse> getLectureHistory(
            @Validated(OrderChecks.class) @RequestBody LectureJudgeHistoryRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String lectureId = request.getLectureId();
        int page = request.getPage();

        User user = userDetails == null ? null : userDetails.toUser();

        List<JudgeHistory> history = service.getLectureHistory(lectureId, page, user);
        int maxPage = service.getLectureHistoryMaxPage(lectureId);

        return ResponseEntity.ok(new JudgeHistoryResponse(history, maxPage));
    }

    @PostMapping("/history/contest")
    public ResponseEntity<JudgeHistoryResponse> getContestHistory(
            @Validated(OrderChecks.class) @RequestBody ContestJudgeHistoryRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String contestId = request.getContestId();
        int page = request.getPage();

        User user = userDetails == null ? null : userDetails.toUser();

        List<JudgeHistory> history = service.getContestHistory(contestId, page, user);
        int maxPage = service.getContestHistoryMaxPage(contestId);

        return ResponseEntity.ok(new JudgeHistoryResponse(history, maxPage));
    }
}
