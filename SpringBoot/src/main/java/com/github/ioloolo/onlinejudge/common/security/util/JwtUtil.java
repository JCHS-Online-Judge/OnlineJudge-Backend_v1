package com.github.ioloolo.onlinejudge.common.security.util;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsImpl;

@Component
public class JwtUtil {

	private final Algorithm algorithm;

	@Value("${app.jwt.expirationDay}")
	private long expirationDay;

	public JwtUtil(@Value("${app.jwt.secret}") String secretKey) {

		this.algorithm = Algorithm.HMAC256(secretKey);
	}

	public String from(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return JWT.create()
				.withSubject("authentication")
				.withIssuedAt(Instant.now())
				.withExpiresAt(Instant.now().plus(expirationDay, TimeUnit.DAYS.toChronoUnit()))
				.withClaim("username", userPrincipal.getUsername())
				.sign(algorithm);
	}

	public String extract(String token) {

		return JWT.require(algorithm).build().verify(token).getClaim("username").asString();
	}

	public boolean validate(String token) {

		if (token == null) {
			return false;
		}

		try {
			JWT.require(algorithm).withSubject("authentication").build().verify(token);

			return true;
		} catch (JWTVerificationException e) {
			return false;
		}
	}
}
