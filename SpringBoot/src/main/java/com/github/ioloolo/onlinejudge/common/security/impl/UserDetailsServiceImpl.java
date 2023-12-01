package com.github.ioloolo.onlinejudge.common.security.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import com.github.ioloolo.onlinejudge.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(ExceptionFactory.of(ExceptionFactory.Type.USER_NOT_FOUND)
																		 .getMessage()));

		return UserDetailsImpl.from(user);
	}
}
