package com.palja.user_service.infrastructure.repository.adapter;

import org.springframework.stereotype.Component;

import com.palja.user_service.domain.repository.ManagerRepository;
import com.palja.user_service.infrastructure.repository.JpaManagerRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ManagerRepositoryAdapter implements ManagerRepository {

	private final JpaManagerRepository jpaManagerRepository;

}
