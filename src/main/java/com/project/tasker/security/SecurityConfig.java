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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.tasker.auth.oauth.OAuthSuccessHandler;

@Configuration
public class SecurityConfig {

	// @formatter:off
		private final static String[] WHITELIST = {
				"/account/password/reset",
				"/auth/**",
				
				"/swagger-ui/**",
				"/swagger-ui.html",
				"/v3/api-docs/**"
		};
	// @formatter:on

	@Bean
	// @formatter:off
	SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity,
											final JwtAuthenticationFilter jwtAuthenticationFilter,
											final OAuthSuccessHandler oAuthSuccessHandler) {
		return httpSecurity
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.authorizeHttpRequests(auth -> {
					auth.requestMatchers("/auth/logout").authenticated();
					
					auth.requestMatchers(WHITELIST).permitAll();
					auth.anyRequest().authenticated();
				})
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.oauth2Login(oAuth -> oAuth.successHandler(oAuthSuccessHandler))
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
