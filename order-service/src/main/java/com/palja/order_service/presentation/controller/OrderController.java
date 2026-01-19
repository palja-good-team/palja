package com.palja.order_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.order_service.application.dto.response.*;
import com.palja.order_service.presentation.dto.request.CancelOrderReq;
import com.palja.order_service.presentation.dto.request.CompleteOrderPaymentReq;
import com.palja.order_service.presentation.dto.request.CreateOrderReq;
import com.palja.order_service.presentation.dto.request.CustomerOrderSearchReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Order", description = "주문 API")
@RequestMapping("/api/v1/orders")
public interface OrderController {

    @Operation(
            summary = "주문 생성",
            description = """
                주문을 생성합니다.
                - 권한: CUSTOMER, MANAGER
                """
    )
    ResponseEntity<ApiResponse<OrderCreateRes>> createOrder(
            @Valid @RequestBody CreateOrderReq request
    );

    @Operation(
            summary = "주문 상세 조회",
            description = """
                    주문 단건을 상세 조회합니다.
                    - 권한: CUSTOMER, MANAGER, COMPANY_USER
                    """
    )
    ResponseEntity<ApiResponse<OrderDetailRes>> getOrderDetail(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable UUID orderId
    );

    @Operation(
            summary = "주문 취소",
            description = """
                    주문을 취소합니다.
                    - 권한: CUSTOMER, MANAGER, COMPANY_USER
                    """
    )
    ResponseEntity<ApiResponse<OrderCancelRes>> cancelOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable UUID orderId,
            @Valid @RequestBody CancelOrderReq request
    );

    @Operation(
            summary = "내 주문 목록 조회(고객)",
            description = """
                    고객 본인의 주문 목록을 조회합니다.
                    - 권한: CUSTOMER
                    - 정렬 기본값: createdAt DESC
                    """
    )
    ResponseEntity<ApiResponse<PageResponse<CustomerOrderSummaryRes>>> getMyOrdersByCustomer(
            @ParameterObject @ModelAttribute CustomerOrderSearchReq request,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "주문 결제 완료 처리(내부 호출)",
            description = """
                    결제 서비스 등 내부 시스템에서 주문 결제 완료 상태로 변경할 때 사용합니다.
                    - 권한: 내부 호출(RequiredInternal)
                    """
    )
    ResponseEntity<ApiResponse<OrderPaymentCompleteRes>> completeOrderPayment(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable UUID orderId,
            @Valid @RequestBody CompleteOrderPaymentReq request
    );
}