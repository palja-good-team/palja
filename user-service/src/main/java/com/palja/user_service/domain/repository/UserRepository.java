package com.palja.user_service.domain.repository;

public interface UserRepository {

	boolean existsByLoginIdAndDeletedAtIsNull(String loginId);

}
