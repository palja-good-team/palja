package com.palja.user_service.infrastructure.repository.adapter;

import org.springframework.stereotype.Component;

import com.palja.user_service.domain.repository.CustomerRepository;
import com.palja.user_service.infrastructure.repository.JpaCustomerRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

	private final JpaCustomerRepository jpaCustomerRepository;

}
