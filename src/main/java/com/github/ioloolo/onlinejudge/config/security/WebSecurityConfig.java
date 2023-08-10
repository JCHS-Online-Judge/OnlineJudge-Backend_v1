package com.github.ioloolo.onlinejudge.config.security;

import javax.ws.rs.HttpMethod;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.github.ioloolo.onlinejudge.config.security.jwt.AuthEntryPoint;
import com.github.ioloolo.onlinejudge.config.security.jwt.AuthTokenFilter;
import com.github.ioloolo.onlinejudge.config.security.jwt.JwtUtil;
import com.github.ioloolo.onlinejudge.config.security.services.UserDetailsServiceImpl;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	private final JwtUtil jwtUtil;
	private final AuthEntryPoint unauthorizedHandler;
	private final UserDetailsServiceImpl userDetailsService;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter(jwtUtil, userDetailsService);
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().disable();
		http.csrf().disable();

		http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		http.authenticationProvider(authenticationProvider());

		http.authorizeRequests()
				.antMatchers("/api/auth/**").permitAll()

				.antMatchers(HttpMethod.PUT, "/api/problem/").hasRole("ADMIN")
				.antMatchers(HttpMethod.PATCH, "/api/problem/").hasRole("ADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/problem/").hasRole("ADMIN")
				.antMatchers("/api/problem/**").permitAll()

				.antMatchers("/api/judge/**").authenticated()

				.anyRequest().permitAll();

		return http.build();
	}
}
