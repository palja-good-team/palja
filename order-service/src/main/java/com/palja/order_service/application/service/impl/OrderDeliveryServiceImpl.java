package com.palja.order_service.application.service.impl;

import com.palja.order_service.application.command.RegisterDeliveryCommand;
import com.palja.order_service.application.dto.response.DeliveryRegisterRes;
import com.palja.order_service.application.service.OrderDeliveryService;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.application.service.validator.OrderDeliveryValidator;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderDeliveryServiceImpl implements OrderDeliveryService {

    private final OrderDeliveryValidator orderDeliveryValidator;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    /**
     * 배송 정보 등록 (송장 번호 등록)
     */
    @Override
    @Transactional
    public DeliveryRegisterRes registerDeliveryTracking(RegisterDeliveryCommand command) {
        Order order = orderService.findOrderWithDetails(command.orderId());
        orderDeliveryValidator.validateDeliveryRegistrationAuthority(order, command.userRole(), command.loginId());
        // 송장 등록 + 주문상태 PREPARING 전환
        order.onTrackingRegistered(command.trackingNumber(), command.courierCompany());
        return DeliveryRegisterRes.from(order);
    }
}