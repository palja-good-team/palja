package com.palja.user_service.domain.repository;

import java.util.Optional;

import com.palja.user_service.domain.entity.User;

public interface UserRepository {

	User save(User user);

	boolean existsByLoginIdAndDeletedAtIsNull(String loginId);

	boolean existsByEmailAndDeletedAtIsNull(String email);

	Optional<User> findByLoginIdAndDeletedAtIsNull(String loginId);

}
