package com.palja.order_service.domain.repository;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findOrderByIdWithItemAndDelivery(UUID orderId);

    Page<Order> findCustomerOrders(
            Long userId, OrderStatus status, Boolean timeDealOrder, LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable
    );

    Page<Order> findCompanyOrders(
            List<UUID> productIds,
            OrderStatus status, Boolean timeDealOrder, LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable
    );
}