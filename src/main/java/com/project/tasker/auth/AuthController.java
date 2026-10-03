package com.project.tasker.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.tasker.auth.request.AuthCodeRequest;
import com.project.tasker.auth.request.AuthMethodRequest;
import com.project.tasker.auth.request.CodeAuthRequest;
import com.project.tasker.auth.request.PasswordAuthRequest;
import com.project.tasker.auth.token.AuthResult;
import com.project.tasker.auth.token.TokenService;
import com.project.tasker.auth.token.refresh.request.RefreshTokenRequest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;
	private final TokenService tokenService;

	@PostMapping("/method")
	public AuthMethod determineAuthenticationMethod(@Valid @RequestBody final AuthMethodRequest request) {
		return this.authService.determineAuthenticationMethod(request.email());
	}

	@PostMapping("/code/request")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void requestAuthenticationCode(@Valid @RequestBody final AuthCodeRequest request) {
		this.authService.requestAuthenticationCode(request.email());
	}

	@PostMapping("/code")
	public AuthResult authenticateWithCode(@Valid @RequestBody final CodeAuthRequest request) {
		return this.authService.authenticateWithCode(request.email(), request.code());
	}

	@PostMapping("/password")
	public AuthResult authenticateWithPassword(@Valid @RequestBody final PasswordAuthRequest request) {
		return this.authService.authenticateWithPassword(request.email(), request.password());
	}

	@PostMapping("/refresh")
	public AuthResult refresh(@Valid @RequestBody final RefreshTokenRequest request) {
		return this.tokenService.refresh(request.refreshToken());
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(@Valid @RequestBody final RefreshTokenRequest request) {
		this.tokenService.revoke(request.refreshToken());
	}

}
