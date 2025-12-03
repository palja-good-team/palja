package com.palja.user_service.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.palja.user_service.domain.entity.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {

	boolean existsByLoginIdAndDeletedAtIsNull(String loginId);

	boolean existsByNameAndDeletedAtIsNull(String name);

	Optional<User> findByLoginIdAndDeletedAtIsNull(String loginId);

}
