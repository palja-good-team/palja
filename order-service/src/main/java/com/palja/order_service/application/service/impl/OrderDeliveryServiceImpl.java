package com.palja.order_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.command.RegisterDeliveryCommand;
import com.palja.order_service.application.dto.response.DeliveryRegisterRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.service.OrderDeliveryService;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.application.service.validator.OrderDeliveryValidator;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.entity.OrderDelivery;
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

    /**
     * 배송 정보 등록 (송장 번호 등록)
     */
    @Override
    @Transactional
    public DeliveryRegisterRes registerDeliveryTracking(RegisterDeliveryCommand command) {
        // 주문 조회
        Order order = orderService.findOrderWithDetails(command.orderId());

        // 권한 검증
        orderDeliveryValidator.validateDeliveryRegistrationAuthority(order, command.userRole(), command.loginId());

        // 배송 정보 존재 확인
        OrderDelivery delivery = order.requireDelivery();

        // 송장 번호 등록 (READY → REQUESTED)
        delivery.registerTracking(command.trackingNumber(), command.courierCompany());

        // 주문 상태 업데이트 (PAID → PREPARING)
        order.markAsPreparing();

        // 저장
        orderService.save(order);

        return DeliveryRegisterRes.from(order);
    }
}