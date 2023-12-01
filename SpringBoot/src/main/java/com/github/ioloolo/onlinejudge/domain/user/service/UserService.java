package com.github.ioloolo.onlinejudge.domain.user.service;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.security.util.JwtUtil;
import com.github.ioloolo.onlinejudge.domain.user.data.Role;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import com.github.ioloolo.onlinejudge.domain.user.repository.RoleRepository;
import com.github.ioloolo.onlinejudge.domain.user.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository repository;
	private final RoleRepository roleRepository;

	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder       encoder;
	private final JwtUtil               jwtUtil;

	@PostConstruct
	public void init() {

		EnumSet<Role.Roles> enumSet = EnumSet.allOf(Role.Roles.class);

		for (Role.Roles role : enumSet) {
			if (roleRepository.findByRole(role).isEmpty()) {
				roleRepository.save(Role.builder().role(role).build());
			}
		}
	}

	public void register(String username, String password) throws Exception {

		if (repository.existsByUsername(username)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_EXIST_USERNAME);
		}

		Role defaultRole = roleRepository.findByRole(Role.Roles.ROLE_USER).orElseThrow();

		User user = User.builder().username(username).password(encoder.encode(password)).role(defaultRole).build();

		repository.save(user);
	}

	public String login(String username, String password) {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
																										  password
		);
		Authentication authentication = authenticationManager.authenticate(authenticationToken);

		return jwtUtil.from(authentication);
	}

	public List<User> getUsers() {

		return repository.findAll();
	}

	public void updatePassword(String userId, String password) throws Exception {

		Optional<User> userOptional = repository.findById(userId);
		if (userOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.USER_NOT_FOUND);
		}
		User user = userOptional.get();

		user.setPassword(encoder.encode(password));

		repository.save(user);
	}

	public void deleteUser(String userId) throws Exception {

		Optional<User> userOptional = repository.findById(userId);
		if (userOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.USER_NOT_FOUND);
		}
		repository.delete(userOptional.get());
	}
}
