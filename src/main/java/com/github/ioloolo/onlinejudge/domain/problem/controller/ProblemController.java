package com.github.ioloolo.onlinejudge.domain.problem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ioloolo.onlinejudge.config.security.services.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.domain.problem.exception.ProblemIdAlreadyExistException;
import com.github.ioloolo.onlinejudge.domain.problem.exception.ProblemNotExistException;
import com.github.ioloolo.onlinejudge.domain.problem.exception.ProblemTitleAlreadyExistException;
import com.github.ioloolo.onlinejudge.domain.problem.model.Problem;
import com.github.ioloolo.onlinejudge.domain.problem.service.ProblemService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/problem")
public class ProblemController {

	private final ProblemService service;

	@PostMapping("/list")
	public ResponseEntity<?> list() {
		return ResponseEntity.ok(service.getProblemList());
	}

	@PostMapping("/{id}")
	public ResponseEntity<?> detail(
			Authentication authentication,
			@PathVariable("id") String id
	) throws ProblemNotExistException {

		return ResponseEntity.ok(
				service.getProblem(id, (UserDetailsImpl) authentication.getPrincipal())
						.orElseThrow(() -> new ProblemNotExistException(id))
		);
	}

	@PutMapping("/")
	public ResponseEntity<?> create(@Validated @RequestBody Problem problem) throws
			ProblemIdAlreadyExistException,
			ProblemTitleAlreadyExistException {

		service.createProblem(problem);

		return ResponseEntity.ok(null);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> edit(
			@PathVariable("id") String id,
			@Validated @RequestBody Problem changed) throws
			ProblemTitleAlreadyExistException,
			ProblemNotExistException,
			ProblemIdAlreadyExistException {

		service.editProblem(id, changed);

		return ResponseEntity.ok(null);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") String id) throws ProblemNotExistException {
		service.removeProblem(id);

		return ResponseEntity.ok(null);
	}
}
