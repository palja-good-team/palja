package com.palja.payment_service.infrastructure.external.adapter;

import com.palja.payment_service.application.dto.response.OrderRes;
import com.palja.payment_service.application.service.OrderService;
// import com.palja.payment_service.infrastructure.external.OrderClient; // TODO: order-service 연동 시 사용
import com.palja.payment_service.infrastructure.external.dto.response.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class OrderAdapter implements OrderService {

    // TODO: 주문 서비스 연동 시 orderClient 주입 및 @RequiredArgsConstructor 추가
    //private final OrderClient orderClient;

    @Override
    public OrderRes getOrderByOrderId(UUID orderId){
        log.debug("주문 조회 요청: orderId={}", orderId);

        // TODO: order-service 연동 시 FeignClient 호출 사용
        //OrderDTO res = orderClient.getOrderByOrderId(orderId).data();

        //임시 더미 데이터
        OrderDTO res = OrderDTO.dummy(orderId);
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
