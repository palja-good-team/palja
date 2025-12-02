package com.palja.order_service.infrastructure.repository;

import com.palja.order_service.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<Order, UUID> {
}