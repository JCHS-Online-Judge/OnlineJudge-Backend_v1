package com.github.ioloolo.onlinejudge.common.security;

import java.io.IOException;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.ioloolo.onlinejudge.common.security.impl.UserDetailsServiceImpl;
import com.github.ioloolo.onlinejudge.common.security.util.JwtUtil;

import jakarta.annotation.Nonnull;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(
			@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain
	) throws ServletException, IOException {

		String token = extract(request);

		if (jwtUtil.validate(token)) {
			String username = jwtUtil.extract(token);

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
																								 null,
																								 userDetails.getAuthorities()
			);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String extract(HttpServletRequest request) {

		String headerAuth = request.getHeader("Authorization");

		return StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ") ? headerAuth.substring(7) : null;
	}

	public Filter getFilter() {

		return this;
	}
}
