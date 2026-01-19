package com.palja.order_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.application.dto.response.DeliveryRegisterRes;
import com.palja.order_service.presentation.dto.request.RegisterDeliveryReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;


@Tag(name = "Delivery", description = "배송 관리 API")
@RequestMapping("/api/v1/orders")
public interface OrderDeliveryController {

    @Operation(
            summary = "배송 정보 등록 (송장 번호 등록)",
            description = """
                    판매자가 송장 번호를 등록하고 배송을 시작합니다.
                    - 권한: COMPANY_USER, MANAGER
                    - 주문 상태가 PAID여야 합니다.
                    - 배송 상태: READY → REQUESTED
                    - 주문 상태: PAID → PREPARING
                    """
    )
    ResponseEntity<ApiResponse<DeliveryRegisterRes>> registerDelivery(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable UUID orderId,
            @Valid @RequestBody RegisterDeliveryReq request
    );
}