package com.project.tasker.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.tasker.account.Account;
import com.project.tasker.account.AccountRepository;
import com.project.tasker.account.AccountService;
import com.project.tasker.auth.token.AuthResult;
import com.project.tasker.auth.token.TokenService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {

	private final AccountRepository accountRepository;
	private final AccountService accountService;
	private final TokenService tokenService;
	private final PasswordEncoder passwordEncoder;

	public AuthMethod determineAuthenticationMethod(final String email) {
		final Account account = this.accountRepository.findByEmail(email).orElse(null);

		if (account == null || !account.hasPassword()) {
			return AuthMethod.CODE;
		}

		return AuthMethod.PASSWORD;
	}

	public void requestAuthenticationCode(final String email) {
		// TODO: Create temporary code
		// TODO: Send email
	}

	public AuthResult authenticateWithCode(final String email, final String code) {
		// TODO: Consume temporary code

		final Account account = this.accountRepository.findByEmail(email).orElseGet(() -> this.accountService.create(email));

		return this.tokenService.issue(account);
	}

	public AuthResult authenticateWithPassword(final String email, final String password) {
		final Account account = this.accountRepository.findByEmail(email).orElseThrow();

		if (!account.hasPassword() || !this.passwordEncoder.matches(password, account.getPasswordHash())) {
			// TODO: Exception: Wrong credentials
		}

		return this.tokenService.issue(account);
	}

}
