package com.project.tasker.account.password;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.tasker.account.Account;
import com.project.tasker.account.AccountRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PasswordService {

	private final PasswordResetTokenService passwordResetTokenService;
	private final AccountRepository accountRepository;

	private final PasswordEncoder passwordEncoder;

	/**
	 * Creates a password reset token and sends it to the account's email address.
	 *
	 * @param accountId the account ID
	 */
	public void requestPasswordReset(final UUID accountId) {
		final Account account = this.accountRepository.findById(accountId).orElseThrow(); // TODO: Exception: Account not found
		final String token = this.passwordResetTokenService.create(account);

		// TODO: Send email notification
	}

	/**
	 * Resets the password of the account associated with the given token.
	 *
	 * @param token       the raw password reset token
	 * @param newPassword the new password
	 */
	@Transactional
	public void resetPassword(final String token, final String newPassword) {
		final Account account = this.passwordResetTokenService.consume(token);
		account.updatePasswordHash(this.passwordEncoder.encode(newPassword));
	}

	/**
	 * Changes the account's password after verifying the current password.
	 *
	 * @param accountId       the account ID
	 * @param currentPassword the current password
	 * @param newPassword     the new password
	 */
	@Transactional
	public void changePassword(final UUID accountId, final String currentPassword, final String newPassword) {
		final Account account = this.accountRepository.findById(accountId).orElseThrow(); // TODO: Exception: Account not found

		if (!account.hasPassword() || !this.passwordEncoder.matches(currentPassword, account.getPasswordHash())) {
			// TODO: Exception: Incorrect password

			account.updatePasswordHash(this.passwordEncoder.encode(newPassword));
		}
	}

}
