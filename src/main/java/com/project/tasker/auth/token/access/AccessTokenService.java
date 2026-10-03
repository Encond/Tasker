package com.project.tasker.auth.token.access;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.project.tasker.account.Account;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AccessTokenService {

	private final AccessTokenProperties accessTokenProperties;
	private final SecretKey secretKey;

	public AccessTokenService(final AccessTokenProperties accessTokenProperties) {
		super();

		this.accessTokenProperties = accessTokenProperties;

		final byte[] secretBytes = accessTokenProperties.secret().getBytes(StandardCharsets.UTF_8);
		this.secretKey = Keys.hmacShaKeyFor(secretBytes);
	}

	public String generate(final Account account) {
		final Instant now = Instant.now();
		final Date issuedAt = Date.from(now);
		final Date expiration = Date.from(now.plus(this.accessTokenProperties.expiration()));

		// @formatter:off
		return Jwts.builder()
				.subject(account.getId().toString())
				.issuedAt(issuedAt)
				.expiration(expiration)
				.signWith(this.secretKey)
				.compact();
		// @formatter:on
	}

	public AccessTokenClaims extractClaims(final String token) {
		// @formatter:off
		final Claims claims = Jwts.parser()
				.verifyWith(this.secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		// @formatter:on

		final UUID accountId = UUID.fromString(claims.getSubject());
		return new AccessTokenClaims(accountId);
	}

}
