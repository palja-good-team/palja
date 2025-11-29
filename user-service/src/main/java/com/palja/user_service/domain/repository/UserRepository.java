package com.palja.user_service.domain.repository;

import java.util.Optional;

import com.palja.user_service.domain.entity.User;

public interface UserRepository {

	boolean existsByLoginIdAndDeletedAtIsNull(String loginId);

	Optional<User> findByLoginIdAndDeletedAtIsNull(String loginId);

}
