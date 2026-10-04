package com.project.tasker.account.oauth;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.tasker.auth.oauth.OAuthProvider;

public interface OAuthIdentityRepository extends JpaRepository<OAuthIdentity, UUID> {

	Optional<OAuthIdentity> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

	Optional<OAuthIdentity> findByAccountIdAndProvider(UUID accountId, OAuthProvider provider);

	boolean existsByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

	boolean existsByAccountIdAndProvider(UUID accountId, OAuthProvider provider);

}
