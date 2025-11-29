package com.palja.user_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.palja.user_service.domain.entity.Customer;

public interface JpaCustomerRepository extends JpaRepository<Customer, UUID> {
}
