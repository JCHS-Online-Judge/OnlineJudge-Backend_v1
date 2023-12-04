package com.github.ioloolo.onlinejudge.domain.judge.controller;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.judge.controller.payload.request.JudgeInfoRequest;
import com.github.ioloolo.onlinejudge.domain.judge.controller.payload.request.JudgeRequest;
import com.github.ioloolo.onlinejudge.domain.judge.controller.payload.response.JudgeInfoResponse;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeLanguage;
import com.github.ioloolo.onlinejudge.domain.judge.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
}
