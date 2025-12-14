package com.palja.payment_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.payment_service.application.dto.response.CancelPaymentRes;
import com.palja.payment_service.application.dto.response.CreatePaymentRes;
import com.palja.payment_service.application.dto.response.ReadPaymentDetailRes;
import com.palja.payment_service.application.dto.response.ReadPaymentSummaryRes;
import com.palja.payment_service.presentation.dto.request.CancelPaymentReq;
import com.palja.payment_service.presentation.dto.request.CompletePaymentReq;
import com.palja.payment_service.presentation.dto.request.CreatePaymentReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Tag(name = "Payment-Controller", description = "결제 관련 API")
public interface PaymentController {

    @Operation(
            summary = "결제 생성",
            description = "사용자의 주문에 대해 PENDING 상태로 결제를 생성합니다."
    )
    ResponseEntity<ApiResponse<CreatePaymentRes>> createPayment(
            @Valid @RequestBody CreatePaymentReq req
    );

    @Operation(
            summary = "결제 완료",
            description = "paymentKey를 받아서 Toss API를 호출하여 결제를 확인하고, 성공 시 APPROVED 상태로 변경합니다."
    )
    ResponseEntity<ApiResponse<CreatePaymentRes>> completePayment(
            @Parameter(description = "결제 ID", example = "660e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID paymentId,
            @Valid @RequestBody CompletePaymentReq req
    );

    @Operation(
            summary = "결제 취소",
            description = "승인된 결제에 대해 PG 취소 요청을 보내고, 성공 시 결제 상태를 취소로 변경합니다."
    )
    ResponseEntity<ApiResponse<CancelPaymentRes>> cancelPayment(
            @Parameter(description = "결제 ID", example = "660e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID paymentId,
            @RequestBody CancelPaymentReq req
    );

    @Operation(
            summary = "결제 단건 조회",
            description = "특정 결제 ID에 대한 상세 정보를 조회합니다."
    )
    ResponseEntity<ApiResponse<ReadPaymentDetailRes>> getPayment(
            @Parameter(description = "결제 ID", example = "660e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID paymentId
    );

    @Operation(
            summary = "결제 목록 조회(검색)",
            description = "결제 상태, 기간, 사용자, 주문ID 등의 조건으로 검색하고, 페이징된 목록을 조회합니다."
    )
    ResponseEntity<ApiResponse<PageResponse<ReadPaymentSummaryRes>>> getPayments(
            @Parameter(description = "결제 상태", example = "APPROVED")
            @RequestParam(required = false) String status,
            @Parameter(description = "사용자 ID", example = "1001")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "주문 ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam(required = false) UUID orderId,
            @Parameter(description = "시작 일시", example = "2025-12-01 00:00:00")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "종료 일시", example = "2025-12-31 23:59:59")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam(required = false) LocalDateTime endDate,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(
            summary = "내 결제 목록 조회",
            description = "로그인한 사용자 자신의 결제 목록을 조회합니다."
    )
    ResponseEntity<ApiResponse<PageResponse<ReadPaymentSummaryRes>>> getMyPayments(
            @Parameter(description = "결제 상태", example = "APPROVED")
            @RequestParam(required = false) String status,
            @Parameter(description = "시작 일시", example = "2025-12-01 00:00:00")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "종료 일시", example = "2025-12-31 23:59:59")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam(required = false) LocalDateTime endDate,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(
            summary = "결제 삭제",
            description = "(관리자)결제 데이터를 삭제합니다. (마스터/매니저만 삭제가 가능합니다.)"
    )
    ResponseEntity<ApiResponse<String>> deletePayment(
            @Parameter(description = "결제 ID", example = "660e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID paymentId
    );
}
