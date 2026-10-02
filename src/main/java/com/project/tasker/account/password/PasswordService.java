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

	public void requestPasswordReset(final UUID accountId) {
		final Account account = this.accountRepository.findById(accountId).orElseThrow(); // TODO: Exception: Account not found
		final String token = this.passwordResetTokenService.create(account);

		// TODO: Send email notification
	}

	@Transactional
	public void resetPassword(final String token, final String newPassword) {
		final Account account = this.passwordResetTokenService.consume(token);
		account.updatePasswordHash(this.passwordEncoder.encode(newPassword));
	}

	@Transactional
	public void changePassword(final UUID accountId, final String currentPassword, final String newPassword) {
		final Account account = this.accountRepository.findById(accountId).orElseThrow(); // TODO: Exception: Account not found

		if (!account.hasPassword() || !this.passwordEncoder.matches(currentPassword, account.getPasswordHash())) {
			// TODO: Exception: Incorrect password

			account.updatePasswordHash(this.passwordEncoder.encode(newPassword));
		}
	}

}
