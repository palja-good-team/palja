package com.palja.order_service.application.service.impl;

import com.palja.order_service.application.command.OrderStatusChangeCommand;
import com.palja.order_service.application.dto.response.OrderStatusChangeRes;
import com.palja.order_service.application.service.OrderManagerService;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.application.service.validator.OrderValidator;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.repository.OrderRepository;
import com.palja.order_service.domain.vo.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderManagerServiceImpl implements OrderManagerService {

    private final OrderRepository orderRepository;

    private final OrderService orderService;
    private final OrderValidator orderValidator;

    /**
     * 주문 상태 변경 (관리자)
     * - 주문 조회
     * - 상태값 검증 및 파싱
     * - 관리자 전용 상태 전환 규칙 검증
     * - 상태 변경 (도메인)
     * - 영속화
     */
    @Override
    @Transactional
    public OrderStatusChangeRes changeOrderStatus(OrderStatusChangeCommand command) {
        log.info("주문 상태 변경 (관리자): orderId={}, targetStatus={}, manager={}",
                command.orderId(), command.status(), command.managerLoginId());

        orderValidator.validateManager(command.managerLoginId());

        Order order = orderService.findOrderWithDetails(command.orderId());
        OrderStatus currentStatus = order.getStatus();
        String previousStatus = currentStatus.name();

        OrderStatus targetStatus = orderValidator.validateAndParseOrderStatus(command.status());

        orderValidator.validateManagerTransition(currentStatus, targetStatus);

        order.changeStatusByManager(targetStatus);
        orderRepository.save(order);

        log.info("주문 상태 변경 완료: orderId={}, {} → {}", command.orderId(), previousStatus, targetStatus);

        return OrderStatusChangeRes.from(order, previousStatus, command.reason(), command.managerLoginId());
    }
}