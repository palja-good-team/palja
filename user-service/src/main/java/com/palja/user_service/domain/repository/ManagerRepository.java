package com.palja.user_service.domain.repository;

import com.palja.user_service.domain.entity.Manager;

public interface ManagerRepository {

	Manager save(Manager manager);

	boolean existsByEmailAndDeletedAtIsNull(String email);

}
