package com.palja.user_service.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.palja.common.vo.UserRole;
import com.palja.user_service.domain.entity.User;

public interface UserRepository {

	User save(User user);

	boolean existsByLoginIdAndDeletedAtIsNull(String loginId);

	boolean existsByEmailAndDeletedAtIsNull(String email);

	Optional<User> findByLoginIdAndDeletedAtIsNull(String loginId);

	Page<User> searchAllManagers(String loginId, String email, String name, Pageable pageable);

	Optional<User> findByLoginIdAndRoleAndDeletedAtIsNull(String loginId, UserRole role);

}
