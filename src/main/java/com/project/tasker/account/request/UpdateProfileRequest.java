package com.project.tasker.account.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(@NotBlank String fullName) {

}
