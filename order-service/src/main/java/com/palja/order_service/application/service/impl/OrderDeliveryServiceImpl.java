package com.palja.order_service.application.service.impl;

import com.palja.order_service.application.command.RegisterDeliveryCommand;
import com.palja.order_service.application.command.UpdateDeliveryStatusCommand;
import com.palja.order_service.application.dto.response.DeliveryRegisterRes;
import com.palja.order_service.application.dto.response.DeliveryStatusRes;
import com.palja.order_service.application.service.OrderDeliveryService;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.application.service.validator.OrderDeliveryValidator;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.entity.OrderDelivery;
import com.palja.order_service.domain.vo.DeliveryStatus;
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
        Order order = orderService.findOrderWithDetails(command.orderId());
        orderDeliveryValidator.validateDeliveryRegistrationAuthority(order, command.userRole(), command.loginId());
        // 송장 등록 + 주문상태 PREPARING 전환
        order.onTrackingRegistered(command.trackingNumber(), command.courierCompany());
        return DeliveryRegisterRes.from(order);
    }

    // 관리자용
    /**
     * 배송 상태 업데이트
     */
    @Override
    @Transactional
    public DeliveryStatusRes updateDeliveryStatus(UpdateDeliveryStatusCommand command) {
        log.info("배송 상태 업데이트 시작: orderId={}, targetStatus={}", command.orderId(), command.status());

        // 1. 주문 조회
        Order order = orderService.findOrderWithDetails(command.orderId());

        OrderDelivery delivery = order.requireDelivery();
        DeliveryStatus currentStatus = delivery.getStatus();
        DeliveryStatus targetStatus = command.status();

        // 2. 동일 상태 변경 요청 처리 (멱등성)
        if (currentStatus == targetStatus) {
            log.info("배송 상태 업데이트 중복 요청 무시 (멱등성): orderId={}, status={}", command.orderId(), targetStatus);
            return DeliveryStatusRes.from(order);
        }

        // 3. 배송 상태 변경
        delivery.transitionTo(targetStatus);
        log.info("배송 상태 전환: orderId={}, currentStatus={}, targetStatus={}",
                command.orderId(), currentStatus, targetStatus);

        // 4. 주문 상태 자동 업데이트
        if (targetStatus == DeliveryStatus.IN_TRANSIT && currentStatus.isBeforeTransit()) {
            // IN_TRANSIT 최초 발생 시: PREPARING → SHIPPED
            order.markAsShipped();
            log.info("주문 상태 자동 변경: orderId={}, orderStatus={} (배송 시작)", command.orderId(), order.getStatus());
        } else if (targetStatus == DeliveryStatus.DELIVERED) {
            // DELIVERED: SHIPPED → DELIVERED
            order.markAsDelivered();
            log.info("주문 상태 자동 변경: orderId={}, orderStatus={} (배송 완료)", command.orderId(), order.getStatus());
        }

        log.info("배송 상태 업데이트 완료: orderId={}, deliveryStatus={}, orderStatus={}",
                command.orderId(), delivery.getStatus(), order.getStatus());

        return DeliveryStatusRes.from(order);
    }
}