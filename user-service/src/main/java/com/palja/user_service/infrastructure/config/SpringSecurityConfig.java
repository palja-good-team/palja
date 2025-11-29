package com.palja.user_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.palja.user_service.infrastructure.security.filter.AuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;

	@Bean
	public AuthenticationManager authenticationManager(
		AuthenticationConfiguration authenticationConfiguration
	) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public AuthenticationFilter authenticationFilter() throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter();
		authenticationFilter.setAuthenticationManager(authenticationManager(authenticationConfiguration));

		return authenticationFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)

			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			.formLogin(AbstractHttpConfigurer::disable)

			.logout(AbstractHttpConfigurer::disable)

			.authorizeHttpRequests(auth -> auth
				.anyRequest().permitAll()
			)

			.addFilterBefore(authenticationFilter(), AuthenticationFilter.class)
		;

		return http.build();
	}

}
