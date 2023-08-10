package com.github.ioloolo.onlinejudge.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ioloolo.onlinejudge.domain.auth.context.request.LoginRequest;
import com.github.ioloolo.onlinejudge.domain.auth.context.request.RegisterRequest;
import com.github.ioloolo.onlinejudge.domain.auth.context.response.JwtResponse;
import com.github.ioloolo.onlinejudge.domain.auth.exception.EmailAlreadyExistException;
import com.github.ioloolo.onlinejudge.domain.auth.exception.UsernameAlreadyExistException;
import com.github.ioloolo.onlinejudge.domain.auth.service.AuthService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService service;

	@PostMapping("/")
	public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();

		String jwtToken = service.login(username, password);

		return ResponseEntity.ok(new JwtResponse(jwtToken));
	}

	@PutMapping("/")
	public ResponseEntity<?> register(@Validated @RequestBody RegisterRequest signUpRequest)
			throws EmailAlreadyExistException, UsernameAlreadyExistException {

		String username = signUpRequest.getUsername();
		String email = signUpRequest.getEmail();
		String password = signUpRequest.getPassword();

		service.register(username, email, password);
		String jwtToken = service.login(username, password);

		return ResponseEntity.ok(new JwtResponse(jwtToken));
	}
}
