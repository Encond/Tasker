package com.project.tasker.account.password;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.tasker.account.password.request.ChangePasswordRequest;
import com.project.tasker.account.password.request.PasswordResetRequest;
import com.project.tasker.security.AuthPrincipal;
import com.project.tasker.security.Principal;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/account/password")
public class PasswordController {

	private final PasswordService passwordService;

	/**
	 * Requests a password reset for the authenticated account.
	 *
	 * @param principal the authenticated account principal
	 */
	@PostMapping("/reset/request")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void requestPasswordReset(@AuthPrincipal final Principal principal) {
		this.passwordService.requestPasswordReset(principal.accountId());
	}

	/**
	 * Resets the password using the given token.
	 *
	 * @param request the password reset information
	 */
	@PostMapping("/reset")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void resetPassword(@Valid @RequestBody final PasswordResetRequest request) {
		this.passwordService.resetPassword(request.token(), request.newPassword());
	}

	/**
	 * Changes the authenticated account's password.
	 *
	 * @param principal the authenticated account principal
	 * @param request   the password change information
	 */
	@PutMapping("/change")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(@AuthPrincipal final Principal principal, @Valid @RequestBody final ChangePasswordRequest request) {
		this.passwordService.changePassword(principal.accountId(), request.currentPassword(), request.newPassword());
	}

}
