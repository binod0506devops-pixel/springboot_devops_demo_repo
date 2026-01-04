package com.Jwt.demo.controller;

import com.Jwt.demo.Model.User;
import com.Jwt.demo.repository.UserRepository;
import com.Jwt.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Layman terms:
 * Endpoints for getting your own profile and (if allowed) managing users.
 * Only users with canManageUsers=true can list, view, update manage flag and delete users.
 *
 * Java/Spring concepts (plain English):
 * - Class & Object (OOP): This class is a blueprint. Spring creates an object to handle requests.
 * - Methods: Functions like me(), listAllUsers() that run when certain URLs are called.
 * - Encapsulation: This controller handles HTTP details and delegates business work to UserService.
 * - @GetMapping/@PutMapping/@DeleteMapping: Connect HTTP verbs (GET/PUT/DELETE) to methods.
 * - Authentication: Spring provides the current logged-in user via the Authentication parameter.
 */
@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Get the current logged-in user's profile
	 */
	@GetMapping("/me")
	public ResponseEntity<User> me(Authentication authentication) {
		String username = authentication.getName();
		Optional<User> user = userRepository.findByUsername(username);
		return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	/**
	 * List all users (requires canManageUsers=true)
	 */
	@GetMapping("/users")
	public ResponseEntity<List<User>> listAllUsers(Authentication authentication) {
		if (!currentCanManage(authentication)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return ResponseEntity.ok(userService.findAll());
	}

	/**
	 * Get a single user by id (requires canManageUsers=true)
	 */
	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUser(@PathVariable Long id, Authentication authentication) {
		if (!currentCanManage(authentication)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return userService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Update a user's manage flag (requires canManageUsers=true)
	 */
	@PutMapping("/users/{id}/canManage")
	public ResponseEntity<User> updateManage(@PathVariable Long id, @RequestParam boolean canManageUsers, Authentication authentication) {
		if (!currentCanManage(authentication)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return ResponseEntity.ok(userService.updateUserManageFlag(id, canManageUsers));
	}

	/**
	 * Delete a user (requires canManageUsers=true)
	 */
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
		if (!currentCanManage(authentication)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		userService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	// Helper: check the current user's manage flag
	private boolean currentCanManage(Authentication authentication) {
		if (authentication == null) return false;
		String username = authentication.getName();
		return userRepository.findByUsername(username)
				.map(User::isCanManageUsers)
				.orElse(false);
	}
}
