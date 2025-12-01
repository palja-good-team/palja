package com.palja.user_service.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.palja.user_service.domain.entity.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {
}
