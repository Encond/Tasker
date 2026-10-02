package com.project.tasker.account.password;

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
public class PasswordResetTokenService {

	public static final Duration EXPIRATION = Duration.ofHours(1);

	private final PasswordResetTokenRepository passwordResetTokenRepository;

	private final SecureRandom secureRandom;

	public String create(final Account account) {
		final String token = this.generateToken();
		final String tokenHash = this.hash(token);
		final Instant expiresAt = Instant.now().plus(EXPIRATION);
		final PasswordResetToken passwordResetToken = PasswordResetToken.of(tokenHash, expiresAt, account);

		this.passwordResetTokenRepository.save(passwordResetToken);

		return token;
	}

	@Transactional
	public Account consume(final String token) {
		final PasswordResetToken passwordResetToken = this.passwordResetTokenRepository.findByTokenHash(this.hash(token)).orElseThrow();

		if (passwordResetToken.isExpired() || passwordResetToken.isUsed()) {
			// TODO: Exception: Invalid password reset token
		}

		passwordResetToken.markAsUsed();

		return passwordResetToken.getAccount();
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
