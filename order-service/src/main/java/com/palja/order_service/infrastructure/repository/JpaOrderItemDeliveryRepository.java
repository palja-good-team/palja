package com.palja.order_service.infrastructure.repository;

import com.palja.order_service.domain.entity.OrderItemDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrderItemDeliveryRepository extends JpaRepository<OrderItemDelivery, UUID> {
}