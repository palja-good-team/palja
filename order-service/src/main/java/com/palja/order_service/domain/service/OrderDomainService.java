package com.palja.order_service.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderDomainService {

    private static final BigDecimal FREE_DELIVERY_THRESHOLD = BigDecimal.valueOf(20000); // 무료배송 기준
    private static final BigDecimal DELIVERY_FEE = BigDecimal.valueOf(3000);             // 기본 배송비

    /**
     * 배송비 계산 (BigDecimal 버전)
     * - 20,000원 이상: 무료 배송
     * - 20,000원 미만: 3,000원
     */
    public BigDecimal calculateDeliveryFee(BigDecimal productTotalAmount) {

        if (productTotalAmount == null) {
            throw new IllegalArgumentException("총 상품 금액이 null 일 수 없습니다.");
        }

        // compareTo 사용 (>= 20000)
        if (productTotalAmount.compareTo(FREE_DELIVERY_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }

        return DELIVERY_FEE;
    }
}