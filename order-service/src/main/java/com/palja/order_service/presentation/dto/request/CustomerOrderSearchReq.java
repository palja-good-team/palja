package com.palja.order_service.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "고객 주문 목록 검색 조건")
public class CustomerOrderSearchReq {

    // 주문 상태
    @Schema(description = "주문 상태", example = "PAID")
    private String status;

    // 시작 날짜 (YYYY-MM-DD)
    @Schema(description = "조회 시작일(yyyy-MM-dd)", example = "2025-12-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    // 종료 날짜 (YYYY-MM-DD)
    @Schema(description = "조회 종료일(yyyy-MM-dd)", example = "2025-12-20")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    // 타임딜 주문 여부
    @Schema(description = "타임딜 주문 여부", example = "true")
    private Boolean timeDealOrder;
}