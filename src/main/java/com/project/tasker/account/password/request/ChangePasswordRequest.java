package com.project.tasker.account.password.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {

}
