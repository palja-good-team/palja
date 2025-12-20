package com.palja.order_service.presentation.controller.impl;

import com.palja.common.annotation.RequiredRole;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.dto.response.OrderStatusChangeRes;
import com.palja.order_service.application.service.OrderManagerService;
import com.palja.order_service.presentation.controller.OrderManagerController;
import com.palja.order_service.presentation.dto.request.OrderStatusChangeReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders/manager")
@RequiredArgsConstructor
public class OrderManagerControllerImpl implements OrderManagerController {

    private final OrderManagerService orderManagerService;

    // 주문 상태 변경 (관리자)
    @PutMapping("/{orderId}/status")
    @RequiredRole(value = {UserRole.MANAGER})
    public ResponseEntity<ApiResponse<OrderStatusChangeRes>> changeOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderStatusChangeReq request
    ) {
        OrderStatusChangeRes response = orderManagerService.changeOrderStatus(request.toCommand(orderId, CurrentUser.getLoginId()));
        return ResponseEntity.ok(ApiResponse.success(response, "주문 상태가 변경되었습니다."));
    }
}