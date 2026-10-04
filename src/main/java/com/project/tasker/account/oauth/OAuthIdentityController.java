package com.project.tasker.account.oauth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.tasker.auth.oauth.OAuthProvider;
import com.project.tasker.security.AuthPrincipal;
import com.project.tasker.security.Principal;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/account/oauth")
public class OAuthIdentityController {

	private final OAuthIdentityService oAuthIdentityService;

	@PostMapping("/{provider}/link")
	public void link(@AuthPrincipal final Principal principal, @PathVariable final OAuthProvider provider) {
		// TODO: Implementation
	}

	@DeleteMapping("/{provider}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unlink(@AuthPrincipal final Principal principal, @PathVariable final OAuthProvider provider) {
		this.oAuthIdentityService.unlink(principal.accountId(), provider);
	}

}
