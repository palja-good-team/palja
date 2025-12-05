package com.palja.order_service.presentation.controller;

import com.palja.common.annotation.RequiredRole;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.dto.OrderCreateRes;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.presentation.dto.request.CreateOrderReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    @RequiredRole(value = {UserRole.MANAGER, UserRole.CUSTOMER})
    public ResponseEntity<ApiResponse<OrderCreateRes>> createOrder(
            @Valid @RequestBody CreateOrderReq request
        ) {

        // Request → Command 변환
        OrderCreateRes response = orderService.createOrder(request.toCommand(CurrentUser.getLoginId()));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "주문이 생성되었습니다."));
    }
}