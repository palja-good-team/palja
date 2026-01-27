package com.palja.order_service.presentation.controller.impl;

import com.palja.common.annotation.RequiredRole;
import com.palja.common.response.ApiResponse;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.dto.response.DeliveryStatusRes;
import com.palja.order_service.application.service.OrderDeliveryService;
import com.palja.order_service.presentation.controller.OrderDeliveryManagerController;
import com.palja.order_service.presentation.dto.request.UpdateDeliveryStatusReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders/manager")
@RequiredArgsConstructor
public class OrderDeliveryManagerControllerImpl implements OrderDeliveryManagerController {

    private final OrderDeliveryService orderDeliveryService;

    @Override
    @PutMapping("/{orderId}/delivery/status")
    @RequiredRole(value = {UserRole.MANAGER})
    public ResponseEntity<ApiResponse<DeliveryStatusRes>> updateDeliveryStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateDeliveryStatusReq request
    ) {
        DeliveryStatusRes response = orderDeliveryService.updateDeliveryStatus(
                request.toCommand(orderId)
        );

        return ResponseEntity.ok(ApiResponse.success(response, "배송 상태가 업데이트되었습니다."));
    }
}