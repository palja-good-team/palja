package com.palja.order_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.application.dto.response.OrderStatusChangeRes;
import com.palja.order_service.presentation.dto.request.OrderStatusChangeReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Order (Manager)", description = "주문 관리자 API")
@RequestMapping("/api/v1/orders/manager")
public interface OrderManagerController {

    @Operation(
            summary = "주문 상태 변경 (관리자)",
            description = """
                    관리자가 주문 상태를 변경합니다.
                    - 권한: MANAGER
                    """
    )
    ResponseEntity<ApiResponse<OrderStatusChangeRes>> changeOrderStatus(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderStatusChangeReq request
    );
}