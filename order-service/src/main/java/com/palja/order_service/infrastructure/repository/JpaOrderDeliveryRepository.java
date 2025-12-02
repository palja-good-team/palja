package com.palja.order_service.infrastructure.repository;

import com.palja.order_service.domain.entity.OrderDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrderDeliveryRepository extends JpaRepository<OrderDelivery, UUID> {
}