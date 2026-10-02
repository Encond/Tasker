package com.project.tasker.account;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "accounts")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(length = 100, nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(length = 100)
	private String passwordHash;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	private Account(final String fullName, final String email) {
		super();

		this.fullName = fullName;
		this.email = email;
	}

	public static Account of(final String fullName, final String email) {
		return new Account(fullName, email);
	}

	public boolean hasPassword() {
		return this.passwordHash != null;
	}

	public void updateFullName(final String fullName) {
		this.fullName = fullName;
	}

	public void updatePasswordHash(final String passwordHash) {
		this.passwordHash = passwordHash;
	}

	@PrePersist
	void onCreate() {
		this.createdAt = Instant.now();
	}

}
