package com.project.tasker.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.tasker.account.Account;
import com.project.tasker.account.AccountRepository;
import com.project.tasker.account.AccountService;
import com.project.tasker.auth.code.TemporaryCodeService;
import com.project.tasker.auth.token.AuthResult;
import com.project.tasker.auth.token.TokenService;
import com.project.tasker.email.EmailService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private AccountService accountService;

	@Mock
	private TokenService tokenService;

	@Mock
	private TemporaryCodeService temporaryCodeService;

	@Mock
	private EmailService emailService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@Test
	void determineAuthenticationMethod_shouldReturnCode_whenAccountDoesNotExist() {
		final String email = "test@example.com";

		when(this.accountRepository.findByEmail(email)).thenReturn(Optional.empty());

		final AuthMethod result = this.authService.determineAuthenticationMethod(email);

		assertEquals(AuthMethod.CODE, result);

		verify(this.accountRepository).findByEmail(email);
	}

	@Test
	void determineAuthenticationMethod_shouldReturnCode_whenAccountHasNoPassword() {
		final String email = "test@example.com";
		final Account account = Account.of("Test Name", email);

		when(this.accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

		final AuthMethod result = this.authService.determineAuthenticationMethod(email);

		assertEquals(AuthMethod.CODE, result);

		verify(this.accountRepository).findByEmail(email);
	}

	@Test
	void determineAuthenticationMethod_shouldReturnPassword_whenAccountHasPassword() {
		final String email = "test@example.com";
		final Account account = Account.of("Test Name", email);
		account.updatePasswordHash("hashed-password");

		when(this.accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

		final AuthMethod result = this.authService.determineAuthenticationMethod(email);

		assertEquals(AuthMethod.PASSWORD, result);

		verify(this.accountRepository).findByEmail(email);
	}

	@Test
	void requestAuthenticationCode_shouldCreateAndSendCode() {
		final String email = "test@example.com";
		final String code = "123456";

		when(this.temporaryCodeService.create(email)).thenReturn(code);

		this.authService.requestAuthenticationCode(email);

		verify(this.temporaryCodeService).create(email);
		verify(this.emailService).sendAuthenticationCode(email, code);
	}

	@Test
	void authenticateWithCode_shouldAuthenticateExistingAccount() {
		final String email = "test@example.com";
		final String code = "123456";

		final Account account = Account.of("Test Name", email);
		final AuthResult authResult = mock(AuthResult.class);

		when(this.accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

		when(this.tokenService.issue(account)).thenReturn(authResult);

		final AuthResult result = this.authService.authenticateWithCode(email, code);

		assertSame(authResult, result);

		verify(this.temporaryCodeService).consume(email, code);
		verify(this.accountRepository).findByEmail(email);
		verify(this.tokenService).issue(account);

		verify(this.accountService, never()).create(anyString());
	}

	@Test
	void authenticateWithCode_shouldCreateAccount_whenAccountDoesNotExist() {
		final String email = "test@example.com";
		final String code = "123456";

		final Account account = Account.of("Test Name", email);
		final AuthResult authResult = mock(AuthResult.class);

		when(this.accountRepository.findByEmail(email)).thenReturn(Optional.empty());

		when(this.accountService.create(email)).thenReturn(account);

		when(this.tokenService.issue(account)).thenReturn(authResult);

		final AuthResult result = this.authService.authenticateWithCode(email, code);

		assertSame(authResult, result);

		verify(this.temporaryCodeService).consume(email, code);
		verify(this.accountRepository).findByEmail(email);
		verify(this.accountService).create(email);
		verify(this.tokenService).issue(account);
	}

	@Test
	void authenticateWithPassword_shouldIssueTokens_whenPasswordIsCorrect() {
		final String email = "test@example.com";
		final String password = "password";

		final Account account = Account.of("Test Name", email);
		account.updatePasswordHash("hashed-password");

		final AuthResult authResult = mock(AuthResult.class);

		when(this.accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

		when(this.passwordEncoder.matches(password, account.getPasswordHash())).thenReturn(true);

		when(this.tokenService.issue(account)).thenReturn(authResult);

		final AuthResult result = this.authService.authenticateWithPassword(email, password);

		assertSame(authResult, result);

		verify(this.accountRepository).findByEmail(email);
		verify(this.passwordEncoder).matches(password, account.getPasswordHash());
		verify(this.tokenService).issue(account);
	}

	@Test
	void authenticateWithPassword_shouldThrow_whenAccountDoesNotExist() {
		final String email = "john@example.com";
		final String password = "password";

		when(this.accountRepository.findByEmail(email)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> this.authService.authenticateWithPassword(email, password));

		verify(this.accountRepository).findByEmail(email);

		verifyNoInteractions(this.passwordEncoder, this.tokenService);
	}

}
