package com.Jwt.demo.service;

import com.Jwt.demo.Model.User;
import com.Jwt.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional
	public User registerUser(String username, String email, String rawPassword, Boolean canManageUsers) {
		if (userRepository.existsByUsername(username)) {
			throw new IllegalArgumentException("Username already taken");
		}
		if (userRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("Email already in use");
		}
		User user = new User(username, email, passwordEncoder.encode(rawPassword));
		if (canManageUsers != null) {
			user.setCanManageUsers(canManageUsers);
		}
		return userRepository.save(user);
	}

	@Transactional
	public User updateUserManageFlag(Long id, boolean canManageUsers) {
		User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.setCanManageUsers(canManageUsers);
		return userRepository.save(user);
	}

	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}
}
