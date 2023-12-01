package com.github.ioloolo.onlinejudge.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ioloolo.onlinejudge.common.validation.OrderChecks;
import com.github.ioloolo.onlinejudge.domain.user.controller.payload.request.DeleteUserRequest;
import com.github.ioloolo.onlinejudge.domain.user.controller.payload.request.LoginRequest;
import com.github.ioloolo.onlinejudge.domain.user.controller.payload.request.RegisterRequest;
import com.github.ioloolo.onlinejudge.domain.user.controller.payload.request.UpdatePasswordRequest;
import com.github.ioloolo.onlinejudge.domain.user.controller.payload.response.TokenResponse;
import com.github.ioloolo.onlinejudge.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

	private final UserService service;

	@PutMapping
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<?> register(
			@Validated(OrderChecks.class) @RequestBody RegisterRequest request
	) throws Exception {

		String username = request.getUsername();
		String password = request.getPassword();

		service.register(username, password);
		String jwtToken = service.login(username, password);

		return ResponseEntity.ok(new TokenResponse(jwtToken));
	}

	@PostMapping
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<?> login(@Validated(OrderChecks.class) @RequestBody LoginRequest request) {

		String username = request.getUsername();
		String password = request.getPassword();

		String jwtToken = service.login(username, password);

		return ResponseEntity.ok(new TokenResponse(jwtToken));
	}

	@PostMapping("/all")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> getUsers() {

		return ResponseEntity.ok(service.getUsers());
	}

	@PatchMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> updatePassword(
			@Validated(OrderChecks.class) @RequestBody UpdatePasswordRequest request
	) throws Exception {

		String userId = request.getUserId();
		String password = request.getNewPassword();

		service.updatePassword(userId, password);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> deleteUser(
			@Validated(OrderChecks.class) @RequestBody DeleteUserRequest request
	) throws Exception {

		String userId = request.getUserId();

		service.deleteUser(userId);

		return ResponseEntity.ok().build();
	}
}
