package com.palja.order_service.presentation.controller.impl;

import com.palja.common.annotation.RequiredRole;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.dto.response.DeliveryRegisterRes;
import com.palja.order_service.application.service.OrderDeliveryService;
import com.palja.order_service.presentation.controller.OrderDeliveryController;
import com.palja.order_service.presentation.dto.request.RegisterDeliveryReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/delivery")
@RequiredArgsConstructor
public class OrderDeliveryControllerImpl implements OrderDeliveryController {

    private final OrderDeliveryService orderService;

    @Override
    @PostMapping
    @RequiredRole(value = {UserRole.COMPANY_USER, UserRole.MANAGER})
    public ResponseEntity<ApiResponse<DeliveryRegisterRes>> registerDelivery(
            @PathVariable UUID orderId,
            @Valid @RequestBody RegisterDeliveryReq request
    ) {
        DeliveryRegisterRes response = orderService.registerDeliveryTracking(
                request.toCommand(orderId, CurrentUser.getLoginId(), CurrentUser.getRole())
        );

        return ResponseEntity.ok(ApiResponse.success(response, "배송 정보가 등록되었습니다."));
    }
}