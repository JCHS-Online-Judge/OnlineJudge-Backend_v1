package com.github.ioloolo.onlinejudge.domain.problem.controller;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.AddLectureProblemRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.LectureProblemsRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.RemoveLectureProblemRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.response.ProblemListResponse;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.service.LectureProblemService;
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
@RequestMapping("/api/problem/lecture")
public class LectureProblemController {

    private final LectureProblemService service;

    @PostMapping("/all")
    public ResponseEntity<ProblemListResponse> getProblems(
            @Validated(OrderChecks.class) @RequestBody LectureProblemsRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {

        String lectureId = request.getLectureId();
        User user = userDetails.toUser();

        List<Problem.Simple> problems = service.getProblems(lectureId, user);

        return ResponseEntity.ok(new ProblemListResponse(problems));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> addProblem(
            @Validated(OrderChecks.class) @RequestBody AddLectureProblemRequest request
    ) throws Exception {

        String lectureId = request.getLectureId();
        String problemId = request.getProblemId();

        service.addProblem(lectureId, problemId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> removeProblem(
            @Validated(OrderChecks.class) @RequestBody RemoveLectureProblemRequest request
    ) throws Exception {

        String lectureId = request.getLectureId();
        String problemId = request.getProblemId();

        service.deleteProblem(lectureId, problemId);

        return ResponseEntity.ok().build();
    }
}
