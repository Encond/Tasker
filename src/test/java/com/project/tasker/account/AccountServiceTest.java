package com.project.tasker.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@InjectMocks
	private AccountService accountService;

	@Test
	void create_shouldSaveAndReturnAccount() {
		final String email = "test@example.com";
		final Account account = Account.of("Test Name", email);

		when(this.accountRepository.existsByEmail(email)).thenReturn(false);
		when(this.accountRepository.save(any(Account.class))).thenReturn(account);

		final Account result = this.accountService.create(email);

		assertEquals(account, result);

		verify(accountRepository).existsByEmail(email);
		verify(accountRepository).save(any(Account.class));
	}

	@Test
	void update_shouldUpdateAndReturnAccount() {
		final UUID id = UUID.randomUUID();
		final Account account = Account.of("Test Name", "test@example.com");

		when(accountRepository.findById(id)).thenReturn(Optional.of(account));
		when(accountRepository.save(account)).thenReturn(account);

		final Account result = accountService.update(id, "Test Name Updated");

		assertEquals(account, result);
		assertEquals("Test Name Updated", account.getFullName());

		verify(accountRepository).findById(id);
		verify(accountRepository).save(account);
	}

	@Test
	void update_shouldThrowWhenAccountDoesNotExist() {
		final UUID id = UUID.randomUUID();

		when(accountRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> accountService.update(id, "Test Name"));

		verify(accountRepository).findById(id);
		verify(accountRepository, never()).save(any());
	}

}
