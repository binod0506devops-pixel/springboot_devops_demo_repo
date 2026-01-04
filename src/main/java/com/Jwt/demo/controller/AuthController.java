package com.Jwt.demo.controller;
import com.Jwt.demo.payload.JwtResponse;
import com.Jwt.demo.Model.User;
import com.Jwt.demo.service.AuthService;
import com.Jwt.demo.service.TokenBlacklistService;
import com.Jwt.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Layman terms:
 * Endpoints for signing up (register) and logging in (get a token).
 * After login you use the token in the Authorization header for protected calls.
 *
 * Java/Spring concepts (plain English):
 * - Class & Object (OOP): This class is a blueprint. Spring creates an object to handle requests.
 * - Methods: Functions inside the class (register, login) that run when an HTTP request arrives.
 * - Constructor: Not written here; Spring creates the object and wires things automatically.
 * - Dependency Injection (@Autowired): Spring provides ready helpers (AuthService, UserService).
 * - @RestController + @RequestMapping: Turn this class into HTTP endpoints under /api/auth.
 * - @PostMapping: The method responds to an HTTP POST.
 * - @RequestParam: Reads simple form/query values like username and password.
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserService userService;
	private final TokenBlacklistService tokenBlacklistService;

	public AuthController(AuthService authService, UserService userService, TokenBlacklistService tokenBlacklistService) {
		this.authService = authService;
		this.userService = userService;
		this.tokenBlacklistService = tokenBlacklistService;
	}

	/**
	 * Register a new user with username, email and password
	 * Supports both JSON body and form parameters
	 */
	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestParam(required = false) @NotBlank String username,
										 @RequestParam(required = false) @Email String email,
										 @RequestParam(required = false) @NotBlank String password,
										 @RequestParam(required = false) Boolean canManageUsers,
										 @RequestBody(required = false) User userRequest) {
		// Handle JSON body input
		if (userRequest != null) {
			User user = userService.registerUser(userRequest.getUsername(), userRequest.getEmail(),
					userRequest.getPassword(), userRequest.isCanManageUsers());
			return ResponseEntity.ok(user);
		}
		// Handle form parameters (backward compatibility)
		User user = userService.registerUser(username, email, password, canManageUsers);
		return ResponseEntity.ok(user);
	}


//	@PostMapping("/register")
//	public ResponseEntity<User> register(@RequestParam User userRequest) {
//		if (userRequest != null) {
//			User user = userService.registerUser(userRequest.getUsername(), userRequest.getEmail(), userRequest.getPassword(), userRequest.isCanManageUsers());
//			return ResponseEntity.ok(user);
//		}
//
//		return ResponseEntity.badRequest().build();
//	}

	/**
	 * Log in and get a JWT token as a plain text response
	 */


	@PostMapping("/login")
	public ResponseEntity<JwtResponse> login(@RequestParam String username,
											 @RequestParam String password) {
		String token = authService.login(username, password);
		return ResponseEntity.ok(new JwtResponse(token));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			tokenBlacklistService.blacklistToken(token);
			return ResponseEntity.ok("Logged out successfully!");
		}
		return ResponseEntity.badRequest().body("Invalid token");
	}
}




