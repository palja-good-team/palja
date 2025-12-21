package com.palja.order_service.infrastructure.external.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealPeriodDTO {
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}