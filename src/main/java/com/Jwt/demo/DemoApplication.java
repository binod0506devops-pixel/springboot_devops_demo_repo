package com.Jwt.demo;

import com.Jwt.demo.Model.User;
import com.Jwt.demo.repository.UserRepository;
import com.Jwt.demo.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {

	@Autowired
	private JwtUtil jwtUtil;

	@Value("${app.jwt.secret:change-me-secret}")
	private String jwtSecret;

	@Value("${app.jwt.expiration-ms:86400000}")
	private long jwtExpirationMs;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner seedDefaults(UserRepository userRepository, PasswordEncoder encoder) {
		return args -> {
			jwtUtil.setSecret(jwtSecret);
			jwtUtil.setJwtExpirationMs(jwtExpirationMs);

			userRepository.findByUsername("admin").orElseGet(() -> {
				User admin = new User("admin", "admin@example.com", encoder.encode("admin123"));
				admin.setCanManageUsers(true);
				return userRepository.save(admin);
			});
		};
	}
}
