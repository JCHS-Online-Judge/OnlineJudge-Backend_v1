package com.github.ioloolo.onlinejudge.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ioloolo.onlinejudge.domain.auth.context.request.IsAdminRequest;
import com.github.ioloolo.onlinejudge.domain.auth.context.request.LoginRequest;
import com.github.ioloolo.onlinejudge.domain.auth.context.request.RegisterRequest;
import com.github.ioloolo.onlinejudge.domain.auth.context.response.IsAdminResponse;
import com.github.ioloolo.onlinejudge.domain.auth.context.response.JwtResponse;
import com.github.ioloolo.onlinejudge.domain.auth.exception.UsernameAlreadyExistException;
import com.github.ioloolo.onlinejudge.domain.auth.service.AuthService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService service;

	@PostMapping("/")
	public ResponseEntity<?> login(@Validated @RequestBody LoginRequest request) {
		String username = request.getUsername();
		String password = request.getPassword();

		String jwtToken = service.login(username, password);

		return ResponseEntity.ok(new JwtResponse(jwtToken));
	}

	@PutMapping("/")
	public ResponseEntity<?> register(@Validated @RequestBody RegisterRequest request)
			throws UsernameAlreadyExistException {

		String username = request.getUsername();
		String password = request.getPassword();

		service.register(username, password);
		String jwtToken = service.login(username, password);

		return ResponseEntity.ok(new JwtResponse(jwtToken));
	}

	@PostMapping("/isAdmin")
	public ResponseEntity<?> isAdmin(@Validated @RequestBody IsAdminRequest request) {
		String username = request.getUsername();

		boolean isAdmin = service.isAdmin(username);

		return ResponseEntity.ok(new IsAdminResponse(isAdmin));
	}
}
