package com.palja.user_service.infrastructure.repository.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.UserRepository;
import com.palja.user_service.infrastructure.repository.JpaUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

	private final JpaUserRepository jpaUserRepository;

	@Override
	public boolean existsByLoginIdAndDeletedAtIsNull(String loginId) {
		return jpaUserRepository.existsByLoginIdAndDeletedAtIsNull(loginId);
	}

	@Override
	public boolean existsByNameAndDeletedAtIsNull(String name) {
		return jpaUserRepository.existsByNameAndDeletedAtIsNull(name);
	}

	@Override
	public Optional<User> findByLoginIdAndDeletedAtIsNull(String loginId) {
		return jpaUserRepository.findByLoginIdAndDeletedAtIsNull(loginId);
	}

}
