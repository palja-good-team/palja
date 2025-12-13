package com.palja.order_service.infrastructure.repository;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderIdAndDeletedAtIsNull(UUID orderId);

    @Query("""
        SELECT o
        FROM Order o
        LEFT JOIN FETCH o.orderItem oi
        LEFT JOIN FETCH o.delivery d
        WHERE o.orderId = :orderId
          AND o.deletedAt IS NULL
    """)
    Optional<Order> findOrderByIdWithItemAndDelivery(@Param("orderId") UUID orderId);

    // ====== 고객용: userId로 주문 목록 조회 ======
    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.orderItem
        WHERE o.userId = :userId
          AND (:status IS NULL OR o.status = :status)
          AND (:timeDealOrder IS NULL OR o.timeDealOrder = :timeDealOrder)
          AND o.createdAt >= :startDate
          AND o.createdAt <= :endDate
          AND o.deletedAt IS NULL
    """)
    Page<Order> findCustomerOrders(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status,
            @Param("timeDealOrder") Boolean timeDealOrder,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // ====== 판매자용: productId 리스트로 주문 목록 조회 ======
    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.orderItem oi
        LEFT JOIN FETCH o.delivery d
        WHERE oi.productId IN :productIds
        AND (:status IS NULL OR o.status = :status)
        AND (:timeDealOrder IS NULL OR o.timeDealOrder = :timeDealOrder)
        AND (:startDate IS NULL OR o.createdAt >= :startDate)
        AND (:endDate IS NULL OR o.createdAt <= :endDate)
        AND o.deletedAt IS NULL
        """)
    Page<Order> findCompanyOrders(
            @Param("productIds") List<UUID> productIds,
            @Param("status") OrderStatus status,
            @Param("timeDealOrder") Boolean timeDealOrder,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}