package com.github.ioloolo.onlinejudge.domain.judge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ioloolo.onlinejudge.config.security.services.UserDetailsImpl;
import com.github.ioloolo.onlinejudge.domain.judge.service.JudgeService;
import com.github.ioloolo.onlinejudge.domain.problem.context.request.JudgeRequest;
import com.github.ioloolo.onlinejudge.domain.problem.exception.ProblemNotExistException;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/judge")
public class JudgeController {

	private final JudgeService service;

	@PutMapping("/")
	public ResponseEntity<?> pushJudgeQueue(
			Authentication authentication,
			@Validated @RequestBody JudgeRequest request)
			throws ProblemNotExistException {

		UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

		service.pushJudgeQueue(user, request);

		return ResponseEntity.ok("");
	}

	@PostMapping("/history")
	public ResponseEntity<?> history(Authentication authentication) {
		UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

		return ResponseEntity.ok(service.getHistory(user));
	}
}
