package com.palja.user_service.infrastructure.security.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		User user = userRepository.findByLoginIdAndDeletedAtIsNull(loginId).orElseThrow(
			() -> new RuntimeException("로그인 정보가 잘못되었습니다.")
		);

		return new UserDetailsImpl(user);
	}

}
