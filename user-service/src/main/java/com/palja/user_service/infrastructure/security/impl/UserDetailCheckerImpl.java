package com.palja.user_service.infrastructure.security.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import com.palja.user_service.domain.vo.UserStatus;

public class UserDetailCheckerImpl implements UserDetailsChecker {

	@Override
	public void check(UserDetails toCheck) {
		if (((UserDetailsImpl) toCheck).getUserStatus().equals(UserStatus.PENDING)) {
			throw new AuthenticationException("회원 가입이 승인 대기 상태입니다.") {
				@Override
				public Authentication getAuthenticationRequest() {
					return super.getAuthenticationRequest();
				}
			};
		}
	}

}
