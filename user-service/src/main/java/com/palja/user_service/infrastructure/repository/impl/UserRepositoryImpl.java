package com.palja.user_service.infrastructure.repository.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.UserRepository;
import com.palja.user_service.infrastructure.repository.DslUserRepository;
import com.palja.user_service.infrastructure.repository.JpaUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final JpaUserRepository jpaUserRepository;
	private final DslUserRepository dslUserRepository;

	@Override
	public User save(User user) {
		return jpaUserRepository.save(user);
	}

	@Override
	public boolean existsByLoginIdAndDeletedAtIsNull(String loginId) {
		return jpaUserRepository.existsByLoginIdAndDeletedAtIsNull(loginId);
	}

	@Override
	public boolean existsByEmailAndDeletedAtIsNull(String email) {
		return jpaUserRepository.existsByEmailAndDeletedAtIsNull(email);
	}

	@Override
	public Optional<User> findByLoginIdAndDeletedAtIsNull(String loginId) {
		return jpaUserRepository.findByLoginIdAndDeletedAtIsNull(loginId);
	}

	@Override
	public Page<User> searchAllManagers(String loginId, String email, String name, Pageable pageable) {
		return dslUserRepository.searchAllManagers(loginId, email, name, pageable);
	}

}
