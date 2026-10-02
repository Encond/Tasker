package com.project.tasker.account;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.tasker.account.request.UpdateProfileRequest;
import com.project.tasker.security.AuthPrincipal;
import com.project.tasker.security.Principal;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {

	private final AccountService accountService;

	@PutMapping("/profile")
	public void updateProfile(@AuthPrincipal final Principal principal, @Valid @RequestBody final UpdateProfileRequest request) {
		this.accountService.update(principal.accountId(), request.fullName());
	}

}
