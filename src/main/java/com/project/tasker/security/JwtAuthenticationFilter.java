package com.project.tasker.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.tasker.auth.token.access.AccessTokenClaims;
import com.project.tasker.auth.token.access.AccessTokenService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final AccessTokenService accessTokenService;

	@Override
	// @formatter:off
	protected void doFilterInternal(final HttpServletRequest request,
									final HttpServletResponse response,
									final FilterChain filterChain) throws ServletException, IOException {
	// @formatter:on
		final String authorization = request.getHeader(AUTHORIZATION_HEADER);

		final boolean isValidAuthorization = authorization != null && authorization.startsWith(BEARER_PREFIX);
		final boolean isAuthorized = SecurityContextHolder.getContext().getAuthentication() != null;
		if (isValidAuthorization && !isAuthorized) {
			this.authenticate(authorization.substring(BEARER_PREFIX.length()));
		}

		filterChain.doFilter(request, response);
	}

	private void authenticate(final String token) {
		try {
			final AccessTokenClaims accessTokenClaims = this.accessTokenService.extractClaims(token);
			final Principal principal = new Principal(accessTokenClaims.accountId());
			final var authentication = new UsernamePasswordAuthenticationToken(principal, null, List.of());

			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (final JwtException | IllegalArgumentException ex) {
			// Invalid bearer tokens are treated as unauthenticated
		}
	}

}
