package com.palja.user_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.palja.user_service.infrastructure.security.impl.UserDetailCheckerImpl;

@Configuration
public class AuthenticationProviderConfig {

	@Bean
	public DaoAuthenticationProvider authenticationProvider(
		UserDetailsService userDetailsService, PasswordEncoder passwordEncoder
	) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		provider.setPostAuthenticationChecks(new UserDetailCheckerImpl()); // Pending 상태 체크
		return provider;
	}
}
