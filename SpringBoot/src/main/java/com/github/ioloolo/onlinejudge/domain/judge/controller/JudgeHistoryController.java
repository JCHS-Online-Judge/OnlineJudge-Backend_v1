package com.github.ioloolo.onlinejudge.domain.judge.controller;

import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.judge.controller.payload.request.history.*;
import com.github.ioloolo.onlinejudge.domain.judge.controller.payload.response.JudgeHistoryResponse;
import com.github.ioloolo.onlinejudge.domain.judge.data.JudgeHistory;
import com.github.ioloolo.onlinejudge.domain.judge.service.JudgeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/judge/history")
public class JudgeHistoryController {

    private final JudgeHistoryService service;

    @PostMapping
    public ResponseEntity<JudgeHistoryResponse> getJudgeHistory(
            @Validated(OrderChecks.class) @RequestBody JudgeHistoryRequest request
    ) throws Exception {

        int page = request.getPage();

        Map.Entry<List<JudgeHistory>, Integer> info = service.getHistory(page);

        return ResponseEntity.ok(new JudgeHistoryResponse(info.getKey(), info.getValue()));
    }

    @PostMapping("/user")
    public ResponseEntity<JudgeHistoryResponse> getJudgeHistoryWithUserFilter(
            @Validated(OrderChecks.class) @RequestBody JudgeHistoryWithUserFilterRequest request
    ) throws Exception {

        String userId = request.getUserId();
        int page = request.getPage();

        Map.Entry<List<JudgeHistory>, Integer> info = service.getHistoryWithUserFilter(userId, page);

        return ResponseEntity.ok(new JudgeHistoryResponse(info.getKey(), info.getValue()));
    }

    @PostMapping("/problem")
    public ResponseEntity<JudgeHistoryResponse> getJudgeHistoryWithProblemFilter(
            @Validated(OrderChecks.class) @RequestBody JudgeHistoryWIthProblemFilterRequest request
    ) throws Exception {

        String problemId = request.getProblemId();
        int page = request.getPage();

        Map.Entry<List<JudgeHistory>, Integer> info = service.getHistoryWithProblemFilter(problemId, page);

        return ResponseEntity.ok(new JudgeHistoryResponse(info.getKey(), info.getValue()));
    }

    @PostMapping("/contest")
    public ResponseEntity<JudgeHistoryResponse> getContestJudgeHistory(
            @Validated(OrderChecks.class) @RequestBody ContestJudgeHistoryRequest request
    ) throws Exception {

        String contestId = request.getContestId();
        int page = request.getPage();

        Map.Entry<List<JudgeHistory>, Integer> info = service.getContestHistory(contestId, page);

        return ResponseEntity.ok(new JudgeHistoryResponse(info.getKey(), info.getValue()));
    }

    @PostMapping("/contest/user")
    public ResponseEntity<JudgeHistoryResponse> getContestJudgeHistoryWithUserFilter(
            @Validated(OrderChecks.class) @RequestBody ContestJudgeHistoryWithUserFilterRequest request
    ) throws Exception {

        String contestId = request.getContestId();
        String userId = request.getUserId();
        int page = request.getPage();

        Map.Entry<List<JudgeHistory>, Integer> info = service.getContestHistoryWithUserFilter(contestId, userId, page);

        return ResponseEntity.ok(new JudgeHistoryResponse(info.getKey(), info.getValue()));
    }

    @PostMapping("/contest/problem")
    public ResponseEntity<JudgeHistoryResponse> getContestJudgeHistoryWithProblemFilter(
            @Validated(OrderChecks.class) @RequestBody ContestJudgeHistoryWIthProblemFilterRequest request
    ) throws Exception {

        String contestId = request.getContestId();
        String problemId = request.getProblemId();
        int page = request.getPage();

        Map.Entry<List<JudgeHistory>, Integer> info = service.getContestHistoryWithProblemFilter(contestId, problemId, page);

        return ResponseEntity.ok(new JudgeHistoryResponse(info.getKey(), info.getValue()));
    }
}
