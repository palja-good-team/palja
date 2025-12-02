package com.palja.user_service.domain.repository;

import com.palja.user_service.domain.entity.Customer;

public interface CustomerRepository {

	Customer save(Customer customer);

	boolean existsByEmailAndDeletedAtIsNull(String email);

}
