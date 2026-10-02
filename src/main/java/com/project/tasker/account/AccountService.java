package com.project.tasker.account;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AccountService {

	private final AccountRepository accountRepository;

	public Account create(final String email) {
		if (this.accountRepository.existsByEmail(email)) {
			// TODO: Exception: Account already exists
		}

		final String fullName = email.split("@")[0]; // TODO: Generate random name instead of exposing email's
		final Account account = Account.of(fullName, email);
		return this.accountRepository.save(account);
	}

	public Account update(final UUID id, final String fullName) {
		final Account account = this.accountRepository.findById(id).orElseThrow(); // TODO: Exception: Account not found

		account.updateFullName(fullName);
		this.accountRepository.save(account);

		return account;
	}

}
