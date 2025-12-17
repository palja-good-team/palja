package com.palja.order_service.application.service;

import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.command.CancelOrderCommand;
import com.palja.order_service.application.command.CompleteOrderPaymentCommand;
import com.palja.order_service.application.command.CreateOrderCommand;
import com.palja.order_service.application.dto.response.*;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.presentation.dto.request.CustomerOrderSearchReq;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    void save(Order order);

    OrderCreateRes createOrder(CreateOrderCommand command);

    void registerPayment(UUID orderId, UUID paymentId);

    OrderDetailRes getOrderDetail(UUID orderId, String loginId, UserRole userRole);

    OrderCancelRes cancelOrder(CancelOrderCommand command);

    Order findOrderWithDetails(UUID orderId);

    PageResponse<CustomerOrderSummaryRes> getMyOrdersByCustomer(
            String loginId, CustomerOrderSearchReq request, Pageable pageable
    );

    OrderPaymentCompleteRes completeOrderPayment(CompleteOrderPaymentCommand command);
}