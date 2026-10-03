package com.project.tasker.auth.token.refresh;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.project.tasker.account.Account;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RefreshTokenService {

	private static final Duration EXPIRATION = Duration.ofDays(7);

	private final RefreshTokenRepository refreshTokenRepository;
	private final SecureRandom secureRandom;

	/**
	 * Creates and saves a refresh token for the given account.
	 *
	 * @param account the account associated with the refresh token
	 * @return the raw refresh token
	 */
	public String create(final Account account) {
		final String token = this.generateToken();
		final String tokenHash = this.hash(token);
		final Instant expiresAt = Instant.now().plus(EXPIRATION);
		final RefreshToken refreshToken = RefreshToken.of(tokenHash, expiresAt, account);

		this.refreshTokenRepository.save(refreshToken);

		return token;
	}

	/**
	 * Invalidates the given refresh token and creates a new one for the same account.
	 *
	 * @param token the refresh token to rotate
	 * @return the new refresh token and its associated account
	 */
	@Transactional
	public RefreshTokenResult rotate(final String token) {
		final RefreshToken refreshToken = this.refreshTokenRepository.findByTokenHash(this.hash(token)).orElseThrow(); // TODO: Exception: RefreshToken not found

		if (refreshToken.isExpired()) {
			this.refreshTokenRepository.delete(refreshToken);

			// TODO: Exception: Refresh token is expired
			throw new IllegalArgumentException("Refresh token is expired");
		}

		final Account account = refreshToken.getAccount();

		this.refreshTokenRepository.delete(refreshToken);

		final String newRefreshToken = this.create(account);

		return new RefreshTokenResult(account, newRefreshToken);
	}

	/**
	 * Revokes the given refresh token.
	 *
	 * @param token the refresh token to revoke
	 */
	@Transactional
	public void revoke(final String token) {
		this.refreshTokenRepository.findByTokenHash(hash(token)).ifPresent(this.refreshTokenRepository::delete);
	}

	private String generateToken() {
		final byte[] bytes = new byte[32];
		this.secureRandom.nextBytes(bytes);

		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private String hash(final String token) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

			return Base64.getEncoder().encodeToString(hash);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 is not available", ex);
		}
	}

}
