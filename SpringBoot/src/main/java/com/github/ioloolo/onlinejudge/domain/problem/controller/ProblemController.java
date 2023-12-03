package com.github.ioloolo.onlinejudge.domain.problem.controller;

import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.CreateProblemRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.DeleteProblemRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.ProblemInfoRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.UpdateProblemRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.response.ProblemInfoResponse;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.response.ProblemListResponse;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problem")
public class ProblemController {

    private final ProblemService service;

    @PostMapping("/all")
    public ResponseEntity<ProblemListResponse> getProblems() {

        List<Problem.Simple> problems = service.getProblems();

        return ResponseEntity.ok(new ProblemListResponse(problems));
    }

    @PostMapping
    public ResponseEntity<ProblemInfoResponse> getProblemInfo(
            @Validated(OrderChecks.class) @RequestBody ProblemInfoRequest request
    ) throws Exception {

        String problemId = request.getProblemId();

        Problem problemInfo = service.getProblemInfo(problemId);

        return ResponseEntity.ok(new ProblemInfoResponse(problemInfo));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> createProblem(
            @Validated(OrderChecks.class) @RequestBody CreateProblemRequest request
    ) throws Exception {

        String problemNumber = request.getProblemNumber();
        String title = request.getTitle();
        String description = request.getDescription();
        String inputDescription = request.getInputDescription();
        String outputDescription = request.getOutputDescription();
        long timeLimit = request.getTimeLimit();
        long memoryLimit = request.getMemoryLimit();
        List<Problem.TestCase> testCases = request.getTestCases();

        service.createProblem(
                problemNumber,
                title,
                description,
                inputDescription,
                outputDescription,
                timeLimit,
                memoryLimit,
                testCases
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateProblem(
            @Validated(OrderChecks.class) @RequestBody UpdateProblemRequest request
    ) throws Exception {

        String problemId = request.getProblemId();
        String problemNumber = request.getProblemNumber();
        String title = request.getTitle();
        String description = request.getDescription();
        String inputDescription = request.getInputDescription();
        String outputDescription = request.getOutputDescription();
        long timeLimit = request.getTimeLimit();
        long memoryLimit = request.getMemoryLimit();
        List<Problem.TestCase> testCases = request.getTestCases();

        service.updateProblem(
                problemId,
                problemNumber,
                title,
                description,
                inputDescription,
                outputDescription,
                timeLimit,
                memoryLimit,
                testCases
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProblem(
            @Validated(OrderChecks.class) @RequestBody DeleteProblemRequest request
    ) throws Exception {

        String problemId = request.getProblemId();

        service.deleteProblem(problemId);

        return ResponseEntity.ok().build();
    }
}
