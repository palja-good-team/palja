package com.palja.order_service.infrastructure.external.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

// 쿠폰 사용 취소 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelCouponDTO {

    private UUID orderId;
}
