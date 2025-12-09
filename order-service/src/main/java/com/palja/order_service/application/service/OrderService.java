package com.palja.order_service.application.service;

import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.command.CancelOrderCommand;
import com.palja.order_service.application.command.CreateOrderCommand;
import com.palja.order_service.application.dto.response.CustomerOrderSummaryRes;
import com.palja.order_service.application.dto.response.OrderCancelRes;
import com.palja.order_service.application.dto.response.OrderCreateRes;
import com.palja.order_service.application.dto.response.OrderDetailRes;
import com.palja.order_service.domain.entity.Order;

import java.time.LocalDate;
import java.util.UUID;

public interface OrderService {
    OrderCreateRes createOrder(CreateOrderCommand command);

    OrderDetailRes getOrderDetail(UUID orderId, String loginId, UserRole userRole);

    OrderCancelRes cancelOrder(CancelOrderCommand command);

    Order findOrderWithDetails(UUID orderId);

    PageResponse<CustomerOrderSummaryRes> getMyOrdersByCustomer(
            String loginId,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            Boolean timeDealOrder,
            Integer page,
            Integer size,
            String sort
    );
}