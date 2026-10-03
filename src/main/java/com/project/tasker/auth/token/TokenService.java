package com.project.tasker.auth.token;

import org.springframework.stereotype.Service;

import com.project.tasker.account.Account;
import com.project.tasker.auth.token.access.AccessTokenService;
import com.project.tasker.auth.token.refresh.RefreshTokenResult;
import com.project.tasker.auth.token.refresh.RefreshTokenService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TokenService {

	private final AccessTokenService accessTokenService;
	private final RefreshTokenService refreshTokenService;

	public AuthResult issue(final Account account) {
		final String accessToken = this.accessTokenService.generate(account);
		final String refreshToken = this.refreshTokenService.create(account);

		return new AuthResult(accessToken, refreshToken);
	}

	public AuthResult refresh(final String refreshToken) {
		final RefreshTokenResult refreshTokenResult = this.refreshTokenService.rotate(refreshToken);
		final String accessToken = this.accessTokenService.generate(refreshTokenResult.account());

		return new AuthResult(accessToken, refreshTokenResult.refreshToken());
	}

	public void revoke(final String refreshToken) {
		this.refreshTokenService.revoke(refreshToken);
	}

}
