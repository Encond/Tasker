package com.project.tasker.account.password;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.tasker.account.Account;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

	@Mock
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Mock
	private SecureRandom secureRandom;

	@InjectMocks
	private PasswordResetTokenService passwordResetTokenService;

	@Test
	void create_shouldSavePasswordResetTokenAndReturnToken() {
		final Account account = Account.of("Test Name", "test@example.com");

		doAnswer(invocation -> {
			final byte[] bytes = invocation.getArgument(0);
			Arrays.fill(bytes, (byte) 1);
			return null;
		}).when(this.secureRandom).nextBytes(any(byte[].class));

		final String token = this.passwordResetTokenService.create(account);

		assertNotNull(token);
		assertFalse(token.isBlank());

		final ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);

		verify(this.passwordResetTokenRepository).save(captor.capture());

		final PasswordResetToken savedToken = captor.getValue();

		assertEquals(account, savedToken.getAccount());
		assertFalse(savedToken.isUsed());
		assertFalse(savedToken.isExpired());
	}

	@Test
	void consume_shouldMarkTokenAsUsedAndReturnAccount() {
		final Account account = Account.of("Test Name", "test@example.com");
		final Instant expiresAt = Instant.now().plus(PasswordResetTokenService.EXPIRATION);
		final PasswordResetToken passwordResetToken = PasswordResetToken.of("some-hash", expiresAt, account);

		when(this.passwordResetTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(passwordResetToken));

		final Account result = this.passwordResetTokenService.consume("some-token");

		assertSame(account, result);
		assertTrue(passwordResetToken.isUsed());

		verify(this.passwordResetTokenRepository).findByTokenHash(anyString());
	}

}
