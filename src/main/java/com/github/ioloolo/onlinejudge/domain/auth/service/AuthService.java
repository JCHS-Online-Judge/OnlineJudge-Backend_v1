package com.github.ioloolo.onlinejudge.domain.auth.service;

import java.util.EnumSet;

import javax.annotation.PostConstruct;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.ioloolo.onlinejudge.common.config.security.jwt.JwtUtil;
import com.github.ioloolo.onlinejudge.domain.auth.exception.UsernameAlreadyExistException;
import com.github.ioloolo.onlinejudge.domain.auth.model.Role;
import com.github.ioloolo.onlinejudge.domain.auth.model.User;
import com.github.ioloolo.onlinejudge.domain.auth.repository.RoleRepository;
import com.github.ioloolo.onlinejudge.domain.auth.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder encoder;
	private final JwtUtil jwtUtil;

	public String login(String username, String password) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		Authentication authentication = authenticationManager.authenticate(authenticationToken);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		return jwtUtil.generateJwtToken(authentication);
	}

	public void register(String username, String password) throws UsernameAlreadyExistException {
		if (userRepository.existsByUsername(username)) {
			throw new UsernameAlreadyExistException();
		}

		Role userRole = roleRepository.findByName(Role.Roles.ROLE_USER)
				.orElseThrow(RuntimeException::new);

		User user = User.builder()
				.username(username)
				.password(encoder.encode(password))
				.role(userRole)
				.build();

		userRepository.save(user);
	}

	public boolean isAdmin(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

		return user.getRoles().contains(roleRepository.findByName(Role.Roles.ROLE_ADMIN).orElseThrow());
	}

	@PostConstruct
	public void initRoleDocuments() {
		EnumSet<Role.Roles> enumSet = EnumSet.allOf(Role.Roles.class);

		for (Role.Roles role : enumSet) {
			if (roleRepository.findByName(role).isEmpty()) {
				roleRepository.save(Role.builder()
						.name(role)
						.build());
			}
		}
	}
}
