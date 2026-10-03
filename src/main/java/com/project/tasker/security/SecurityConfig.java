package com.project.tasker.security;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	// @formatter:off
		private final static String[] WHITELIST = {
				"/account/password/reset",
				
				"/swagger-ui/**",
				"/swagger-ui.html",
				"/v3/api-docs/**"
		};
		// @formatter:on

	@Bean
	SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) {
		// @formatter:off
		return httpSecurity
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.authorizeHttpRequests(auth -> {
					auth.requestMatchers(WHITELIST).permitAll();
					auth.anyRequest().authenticated();
				})
				.build();
		// @formatter:on
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecureRandom secureRandom() {
		return new SecureRandom();
	}

}
