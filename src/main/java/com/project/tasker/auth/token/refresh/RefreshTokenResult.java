package com.project.tasker.auth.token.refresh;

import com.project.tasker.account.Account;

public record RefreshTokenResult(Account account, String refreshToken) {

}
