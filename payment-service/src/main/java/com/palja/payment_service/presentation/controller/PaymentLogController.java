package com.palja.payment_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.payment_service.application.dto.response.ReadPaymentLogRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "PaymentLog-Controller", description = "결제 로그 관련 API")
public interface PaymentLogController {

    @Operation(
            summary = "결제 로그 단건 조회",
            description = "특정 결제ID에 대한 PG 요청/응답 정보를 조회합니다."
    )
    @GetMapping("/api/v1/payments/{paymentId}/logs")
    ResponseEntity<ApiResponse<List<ReadPaymentLogRes>>> getPaymentLogsByPaymentId(
            @Parameter(description = "결제 ID", example = "660e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID paymentId
    );

    @Operation(
            summary = "결제 로그 목록 조회(검색)",
            description = "결제 ID, 기간, 상태 등의 조건으로 결제 로그를 검색 및 조회합니다."
    )
    @GetMapping("/api/v1/payment-logs")
    ResponseEntity<ApiResponse<PageResponse<ReadPaymentLogRes>>> getPaymentLogs(
            @Parameter(description = "결제 ID", example = "660e8400-e29b-41d4-a716-446655440001")
            @RequestParam(required = false) UUID paymentId,
            @Parameter(description = "주문 ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam(required = false) UUID orderId,
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
            summary = "결제 로그 삭제",
            description = "결제 로그 데이터를 삭제합니다. (스케줄러 사용해서 1년 뒤)"
    )
    @PostMapping("/api/v1/payment-logs/delete")
    ResponseEntity<ApiResponse<String>> deleteOldLogs();
}
