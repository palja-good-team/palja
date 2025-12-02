package com.palja.order_service.infrastructure.repository;

import com.palja.order_service.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrderItemRepository extends JpaRepository<OrderItem, UUID> {
}