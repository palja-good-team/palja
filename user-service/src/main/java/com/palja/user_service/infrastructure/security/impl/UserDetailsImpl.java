package com.palja.user_service.infrastructure.security.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.vo.UserStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

	private final User user;

	public Long getUserId() {
		return user.getId();
	}

	public String getUserRole() {
		return user.getRole().name();
	}

	public UserStatus getUserStatus() {
		return user.getStatus();
	}

	@Override
	public String getUsername() {
		return user.getLoginId();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(user.getRole().getAuthority()));
	}

}
