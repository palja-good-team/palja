package com.palja.order_service.presentation.controller;

import com.palja.common.annotation.RequiredRole;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.dto.response.CustomerOrderSummaryRes;
import com.palja.order_service.application.dto.response.OrderCancelRes;
import com.palja.order_service.application.dto.response.OrderCreateRes;
import com.palja.order_service.application.dto.response.OrderDetailRes;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.presentation.dto.request.CancelOrderReq;
import com.palja.order_service.presentation.dto.request.CreateOrderReq;
import com.palja.order_service.presentation.dto.request.CustomerOrderSearchReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    @RequiredRole(value = {UserRole.MANAGER, UserRole.CUSTOMER, UserRole.COMPANY_USER})
    public ResponseEntity<ApiResponse<OrderDetailRes>> getOrderDetail(
            @PathVariable UUID orderId
    ) {
        OrderDetailRes response = orderService.getOrderDetail(orderId, CurrentUser.getLoginId(), CurrentUser.getRole());
        return ResponseEntity.ok(ApiResponse.success(response, "주문이 조회되었습니다."));
    }

    // 주문 취소
    @PostMapping("/{orderId}/cancel")
    @RequiredRole(value = {UserRole.MANAGER, UserRole.CUSTOMER, UserRole.COMPANY_USER})
    public ResponseEntity<ApiResponse<OrderCancelRes>> cancelOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody CancelOrderReq request
    ) {
        OrderCancelRes response = orderService.cancelOrder(request.toCommand(orderId, CurrentUser.getLoginId(), CurrentUser.getRole()));
        return ResponseEntity.ok(ApiResponse.success(response,"주문이 취소되었습니다."));
    }

    // 내 주문 목록 조회
    @GetMapping("/customer/me")
    @RequiredRole({UserRole.CUSTOMER})
    public ResponseEntity<ApiResponse<PageResponse<CustomerOrderSummaryRes>>> getMyOrdersByCustomer(
            @ModelAttribute CustomerOrderSearchReq request,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        PageResponse<CustomerOrderSummaryRes> response = orderService.getMyOrdersByCustomer(
                CurrentUser.getLoginId(), request, pageable
        );
        return ResponseEntity.ok(ApiResponse.success(response, "고객용 주문 목록이 조회되었습니다.")
        );
    }

}