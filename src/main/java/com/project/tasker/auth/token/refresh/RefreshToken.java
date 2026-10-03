package com.project.tasker.auth.token.refresh;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, updatable = false)
	private String tokenHash;

	@Column(nullable = false)
	private Instant expiresAt;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	private Account account;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	private RefreshToken(final String tokenHash, final Instant expiresAt, final Account account) {
		super();

		this.tokenHash = tokenHash;
		this.expiresAt = expiresAt;
		this.account = account;
	}

	public static RefreshToken of(final String tokenHash, final Instant expiresAt, final Account account) {
		return new RefreshToken(tokenHash, expiresAt, account);
	}

	public boolean isExpired() {
		return !this.expiresAt.isAfter(Instant.now());
	}

	@PrePersist
	void onCreate() {
		this.createdAt = Instant.now();
	}

}
