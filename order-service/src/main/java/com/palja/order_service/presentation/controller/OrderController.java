package com.palja.order_service.presentation.controller;

import com.palja.common.annotation.RequiredRole;
import com.palja.common.response.ApiResponse;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.dto.CreateOrderRes;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.presentation.dto.request.CreateOrderReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    @RequiredRole(value = {UserRole.MANAGER, UserRole.CUSTOMER})
    public ResponseEntity<ApiResponse<CreateOrderRes>> createOrder(
            @Valid @RequestBody CreateOrderReq request,
            // TODO: 추후 AuditorContext로 변경
            @RequestHeader("X-Login-Id") String loginId,
            @RequestHeader("X-User-Role") UserRole userRole
        ) {

        // Request → Command 변환
        CreateOrderRes response = orderService.createOrder(request.toCommand(loginId));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "주문이 생성되었습니다."));
    }
}