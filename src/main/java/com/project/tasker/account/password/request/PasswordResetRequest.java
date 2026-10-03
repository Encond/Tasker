package com.project.tasker.account.password.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank String token, @NotBlank String newPassword) {

}
