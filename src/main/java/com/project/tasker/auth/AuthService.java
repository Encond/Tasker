package com.project.tasker.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.tasker.account.Account;
import com.project.tasker.account.AccountRepository;
import com.project.tasker.account.AccountService;
import com.project.tasker.auth.code.TemporaryCodeService;
import com.project.tasker.auth.token.AuthResult;
import com.project.tasker.auth.token.TokenService;
import com.project.tasker.email.EmailService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {

	private final AccountRepository accountRepository;
	private final AccountService accountService;
	private final TokenService tokenService;
	private final TemporaryCodeService temporaryCodeService;
	private final EmailService emailService;

	private final PasswordEncoder passwordEncoder;

	/**
	 * Determines the authentication method available for the given email.
	 *
	 * @param email the email address to authenticate
	 * @return the authentication method available for the email
	 */
	public AuthMethod determineAuthenticationMethod(final String email) {
		final Account account = this.accountRepository.findByEmail(email).orElse(null);

		if (account == null || !account.hasPassword()) {
			return AuthMethod.CODE;
		}

		return AuthMethod.PASSWORD;
	}

	/**
	 * Creates and sends an authentication code to the given email address.
	 *
	 * @param email the email address to send the code to
	 */
	public void requestAuthenticationCode(final String email) {
		final String temporaryCode = this.temporaryCodeService.create(email);
		this.emailService.sendAuthenticationCode(email, temporaryCode);
	}

	/**
	 * Authenticates an account using a temporary authentication code. Creates the
	 * account if no account exists for the given email.
	 *
	 * @param email the email address to authenticate
	 * @param code  the authentication code
	 * @return the issued access and refresh tokens
	 */
	public AuthResult authenticateWithCode(final String email, final String code) {
		this.temporaryCodeService.consume(email, code);

		final Account account = this.accountRepository.findByEmail(email).orElseGet(() -> this.accountService.create(email));

		return this.tokenService.issue(account);
	}

	/**
	 * Authenticates an account using its password.
	 *
	 * @param email    the account's email address
	 * @param password the account's password
	 * @return the issued access and refresh tokens
	 */
	public AuthResult authenticateWithPassword(final String email, final String password) {
		final Account account = this.accountRepository.findByEmail(email).orElseThrow();

		if (!account.hasPassword() || !this.passwordEncoder.matches(password, account.getPasswordHash())) {
			// TODO: Exception: Wrong credentials
		}

		return this.tokenService.issue(account);
	}

}
