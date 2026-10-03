package com.project.tasker.auth.token.access;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth.token.access")
public record AccessTokenProperties(String secret, Duration expiration) {

}
