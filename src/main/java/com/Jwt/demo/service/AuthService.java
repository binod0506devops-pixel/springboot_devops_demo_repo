package com.Jwt.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Layman terms:
 * This class logs a user in. You give username + password, it checks them,
 * and if correct it gives you back a short "ticket" (JWT token) to use on future calls.
 *
 * Java/Spring concepts:
 * - @Service: Marks this class as a business/service component.
 * - Dependency Injection (@Autowired): Spring gives us ready-to-use objects.
 * - AuthenticationManager: Built-in Spring Security component that validates credentials.
 * - JWT: A signed string we generate to prove the user is logged in.
 */
@Service
public class AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * Try to log in with username and password. If success, return a JWT token string.
	 */
	public String login(String username, String password) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password)
		);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		return jwtUtil.generateToken(userDetails.getUsername());
	}
}
