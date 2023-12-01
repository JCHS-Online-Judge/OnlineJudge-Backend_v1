package com.github.ioloolo.onlinejudge.domain.problem.controller;

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
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.CreateLectureProblemRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.DeleteProblemRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.LectureProblemsRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.ProblemInfoRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.request.UpdateProblemRequest;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.response.ProblemInfoResponse;
import com.github.ioloolo.onlinejudge.domain.problem.controller.payload.response.ProblemsResponse;
import com.github.ioloolo.onlinejudge.domain.problem.data.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.service.LectureProblemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problem/lecture")
public class LectureProblemController {

	private final LectureProblemService service;

	@PostMapping("/all")
	public ResponseEntity<?> getProblems(
			@Validated(OrderChecks.class) @RequestBody LectureProblemsRequest request,
			@AuthenticationPrincipal UserDetailsImpl userDetails
	) throws Exception {

		String lectureId = request.getLectureId();

		return ResponseEntity.ok(new ProblemsResponse(service.getProblems(lectureId, userDetails.toUser())));
	}

	@PostMapping
	public ResponseEntity<?> getProblemInfo(
			@Validated(OrderChecks.class) @RequestBody ProblemInfoRequest request,
			@AuthenticationPrincipal UserDetailsImpl userDetails
	) throws Exception {

		return ResponseEntity.ok(new ProblemInfoResponse(service.getProblemInfo(request.getProblemId(),
																				userDetails.toUser()
		)));
	}

	@PutMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> createProblem(
			@Validated(OrderChecks.class) @RequestBody CreateLectureProblemRequest request
	) throws Exception {

		String lectureId = request.getLectureId();

		String problemNumber = request.getProblemNumber();
		String title = request.getTitle();
		String description = request.getDescription();
		String inputDescription = request.getInputDescription();
		String outputDescription = request.getOutputDescription();
		long timeLimit = request.getTimeLimit();
		long memoryLimit = request.getMemoryLimit();
		List<Problem.TestCase> testCases = request.getTestCases();

		service.createProblem(lectureId,
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
	public ResponseEntity<?> updateProblem(
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

		service.updateProblem(problemId,
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
	public ResponseEntity<?> deleteProblem(
			@Validated(OrderChecks.class) @RequestBody DeleteProblemRequest request
	) throws Exception {

		String problemId = request.getProblemId();

		service.deleteProblem(problemId);

		return ResponseEntity.ok().build();
	}
}
