package com.palja.order_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.application.dto.response.DeliveryStatusRes;
import com.palja.order_service.presentation.dto.request.UpdateDeliveryStatusReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;


@Tag(name = "Delivery (Manager)", description = "배송 관리자 API")
@RequestMapping("/api/v1/orders/manager")
public interface OrderDeliveryManagerController {

    @Operation(
            summary = "배송 상태 업데이트",
            description = """
                    배송 상태를 업데이트합니다.
                    - 권한: MANAGER (배송 추적 시스템 역할)
                    - 상태 전이: READY → REQUESTED → IN_TRANSIT → DELIVERED
                    - IN_TRANSIT 최초 발생 시: 주문 상태 → SHIPPED
                    - DELIVERED 발생 시: 주문 상태 → DELIVERED
                    - 멱등성 보장: 동일 상태 변경 요청 시 200 OK 반환
                    """
    )
    ResponseEntity<ApiResponse<DeliveryStatusRes>> updateDeliveryStatus(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateDeliveryStatusReq request
    );
}