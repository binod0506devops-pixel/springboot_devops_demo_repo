package com.Jwt.demo.Model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Layman terms:
 * This class represents a user record in the database (a table row).
 *
 * Java/JPA concepts:
 * - @Entity: Marks this class to be stored in a database table.
 * - @Id/@GeneratedValue: Auto-generated numeric id.
 * - @Column + validation annotations: Basic rules and column settings.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"username"}),
		@UniqueConstraint(columnNames = {"email"})
})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 3, max = 50)
	@Column(nullable = false, length = 50)
	private String username;

//	@NotBlank
@Column(length = 120)
	private String email;

	@NotBlank
	@Size(min = 6)
	@Column(nullable = false)
	private String password;

	// If true, this user can manage other users (list, update, delete)
	@Column(nullable = false)
	private boolean canManageUsers = false;

	public User() {}

	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public boolean isCanManageUsers() { return canManageUsers; }
	public void setCanManageUsers(boolean canManageUsers) { this.canManageUsers = canManageUsers; }
}
