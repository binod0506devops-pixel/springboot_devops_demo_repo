package com.Jwt.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Layman terms:
 * This class sets the security rules. Some URLs are open (login, docs), others need a token.
 * We tell Spring Security to use our JWT filter and not to create server sessions (stateless).
 *
 * Java/Spring concepts:
 * - @Configuration + @Bean: Define Spring-managed objects (beans).
 * - PasswordEncoder (BCrypt): Safely stores passwords (hashed, not plain text).
 * - SecurityFilterChain: The chain of filters (like checkpoints) for every request.
 */
@Configuration
public class SecurityConfig {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		return auth.build();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
			// No server sessions; each request must bring its own token
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			// Public endpoints (no token needed)
			.antMatchers(
				"/api/auth/**",
				"/h2-console/**",
				"/v3/api-docs/**",
				"/swagger-ui/**",
				"/swagger-ui.html"
			).permitAll()
			// Everything else requires a valid JWT token
			.anyRequest().authenticated();

		// Needed for H2 console to render in browser
		http.headers().frameOptions().sameOrigin();
		// Insert our JWT checker before the username/password filter
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
