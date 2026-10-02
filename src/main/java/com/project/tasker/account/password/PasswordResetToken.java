package com.project.tasker.account.password;

import java.time.Instant;
import java.util.UUID;

import com.project.tasker.account.Account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, updatable = false)
	private String tokenHash;

	@Column(nullable = false, updatable = false)
	private Instant expiresAt;

	@Column(nullable = false)
	private boolean isUsed;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	private Account account;

	private PasswordResetToken(final String tokenHash, final Instant expiresAt, final Account account) {
		super();

		this.tokenHash = tokenHash;
		this.expiresAt = expiresAt;
		this.account = account;
	}

	public static PasswordResetToken of(final String tokenHash, final Instant expiresAt, final Account account) {
		return new PasswordResetToken(tokenHash, expiresAt, account);
	}

	public boolean isExpired() {
		return Instant.now().isAfter(this.expiresAt);
	}

	public void markAsUsed() {
		this.isUsed = true;
	}

}
