package com.project.tasker.account.oauth;

import java.util.UUID;

import com.project.tasker.account.Account;
import com.project.tasker.auth.oauth.OAuthProvider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "oauth_identities")
public class OAuthIdentity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OAuthProvider provider;

	@Column(nullable = false)
	private String providerUserId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	private Account account;

	private OAuthIdentity(final OAuthProvider provider, final String providerUserId, final Account account) {
		super();

		this.provider = provider;
		this.providerUserId = providerUserId;
		this.account = account;
	}

	public static OAuthIdentity of(final OAuthProvider provider, final String providerUserId, final Account account) {
		return new OAuthIdentity(provider, providerUserId, account);
	}

}
