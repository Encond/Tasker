package com.project.tasker.account.oauth;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.tasker.account.Account;
import com.project.tasker.account.AccountRepository;
import com.project.tasker.auth.oauth.OAuthProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OAuthIdentityService {

	private final OAuthIdentityRepository oAuthIdentityRepository;
	private final AccountRepository accountRepository;

	/**
	 * Links an OAuth identity to an account.
	 *
	 * @param accountId      the ID of the account to link the OAuth identity to
	 * @param provider       the OAuth provider
	 * @param providerUserId the user's ID assigned by the OAuth provider
	 */
	@Transactional
	public void link(final UUID accountId, final OAuthProvider provider, final String providerUserId) {
		final Account account = this.accountRepository.findById(accountId).orElseThrow(); // TODO: Exception: Account not found

		final boolean isAlreadyLinked = this.oAuthIdentityRepository.existsByProviderAndProviderUserId(provider, providerUserId);
		if (isAlreadyLinked) {
			// TODO: Exception: OAuthIdentity is already linked
		}

		final boolean isAlreadyLinkedToAccount = this.oAuthIdentityRepository.existsByAccountIdAndProvider(accountId, provider);
		if (isAlreadyLinkedToAccount) {
			// TODO: Exception: OAuthIdentity is already linked to this Account
		}

		final OAuthIdentity oAuthIdentity = OAuthIdentity.of(provider, providerUserId, account);
		this.oAuthIdentityRepository.save(oAuthIdentity);
	}

	/**
	 * Unlinks an OAuth identity from an account.
	 *
	 * @param accountId the ID of the account to unlink the OAuth identity from
	 * @param provider  the OAuth provider
	 */
	@Transactional
	public void unlink(final UUID accountId, final OAuthProvider provider) {
		if (!this.accountRepository.existsById(accountId)) {
			// TODO: Exception: Account not found
		}
	
		final OAuthIdentity oAuthIdentity = this.oAuthIdentityRepository.findByAccountIdAndProvider(accountId, provider).orElseThrow(); // TODO: Exception: OAuthIdentity is not linked
		this.oAuthIdentityRepository.delete(oAuthIdentity);
	}

}
