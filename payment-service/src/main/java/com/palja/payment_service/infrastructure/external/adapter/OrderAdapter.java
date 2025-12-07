package com.palja.payment_service.infrastructure.external.adapter;

import com.palja.payment_service.application.dto.response.OrderRes;
import com.palja.payment_service.application.service.OrderService;
import com.palja.payment_service.infrastructure.external.OrderClient;
import com.palja.payment_service.infrastructure.external.dto.response.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAdapter implements OrderService {

    private final OrderClient orderClient;

    @Override
    public OrderRes getOrderByOrderId(UUID orderId){
        log.debug("주문 조회 요청: orderId={}", orderId);

        OrderDTO res = orderClient.getOrderByOrderId(orderId).data();
        return toOrderRes(res);
    }

    private OrderRes toOrderRes(OrderDTO dto) {
        return OrderRes.of(
                dto.getOrderId(),
                dto.getUserId(),
                dto.getStatus(),
                dto.getFinalAmount()
        );
    }
}
