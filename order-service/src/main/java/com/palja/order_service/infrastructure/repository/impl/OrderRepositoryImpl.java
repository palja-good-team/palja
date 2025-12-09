package com.palja.order_service.infrastructure.repository.impl;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.repository.OrderRepository;
import com.palja.order_service.domain.vo.OrderStatus;
import com.palja.order_service.infrastructure.repository.JpaOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    @Transactional
    public Order save(Order order) {
        return jpaOrderRepository.save(order);
    }

    @Override
    public Optional<Order> findOrderByIdWithItemAndDelivery(UUID orderId) {
        return jpaOrderRepository.findOrderByIdWithItemAndDelivery(orderId);
    }

    // ====== 고객용: 주문 목록 조회 ======
    @Override
    public Page<Order> findCustomerOrders(
            Long userId, OrderStatus status, Boolean timeDealOrder, LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable
    ) {
        return jpaOrderRepository.findCustomerOrders(userId, status, timeDealOrder, startDate, endDate, pageable);
    }

    // ====== 판매자의 주문 목록 조회 (판매 상품 기준) ======
    @Override
    public Page<Order> findCompanyOrders(
            List<UUID> productIds,
            OrderStatus status, Boolean timeDealOrder, LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable
    ) {

        // 상품이 없으면 빈 페이지 반환
        if (productIds == null || productIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return jpaOrderRepository.findCompanyOrders(productIds, status, timeDealOrder, startDate, endDate, pageable);
    }
}