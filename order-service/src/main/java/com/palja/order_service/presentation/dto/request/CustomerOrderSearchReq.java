package com.palja.order_service.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerOrderSearchReq {

    // 주문 상태
    private String status;

    // 시작 날짜 (YYYY-MM-DD)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    // 종료 날짜 (YYYY-MM-DD)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    // 타임딜 주문 여부
    private Boolean timeDealOrder;
}