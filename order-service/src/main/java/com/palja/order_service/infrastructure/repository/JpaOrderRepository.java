package com.palja.order_service.infrastructure.repository;

import com.palja.order_service.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
        SELECT o
        FROM Order o
        LEFT JOIN FETCH o.orderItem oi
        LEFT JOIN FETCH o.delivery d
        WHERE o.orderId = :orderId
          AND o.deletedAt IS NULL
    """)
    Optional<Order> findOrderByIdWithItemAndDelivery(@Param("orderId") UUID orderId);
}