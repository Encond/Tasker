package com.project.tasker.auth.code;

import java.security.SecureRandom;
import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TemporaryCodeService {

	private static final Duration EXPIRATION = Duration.ofMinutes(5);

	private final StringRedisTemplate stringRedisTemplate;
	private final SecureRandom secureRandom;

	public String create(final String email) {
		final String code = String.format("%06d", this.secureRandom.nextInt(1_000_000));
		final String key = this.key(email);
		this.stringRedisTemplate.opsForValue().set(key, code, EXPIRATION);

		return code;
	}

	public void consume(final String email, final String code) {
		final String key = this.key(email);
		final String storedCode = this.stringRedisTemplate.opsForValue().get(key);

		if (storedCode == null || !storedCode.equals(code)) {
			// TODO: Exception: Wrong code | swap "return" with "throw"
			return;
		}

		this.stringRedisTemplate.delete(key);
	}

	private String key(final String email) {
		return "auth:code:" + email;
	}

}
