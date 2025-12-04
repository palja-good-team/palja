package com.palja.order_service.application.dto;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.infrastructure.external.dto.response.ProductDTO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class ProductRes {
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;

    // 외부 Product API 응답 → 주문용 ProductRes 변환
    public static ProductRes from(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND);
        }

        return ProductRes.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getName())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStock())
                .build();
    }

    // 총 상품 금액 계산 (단가 × 수량)
    public BigDecimal calculateProductTotalAmount(int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // 재고 검증
    public void validateStock(int quantity) {
        if (stockQuantity < quantity) {
            throw new BusinessException(OrderErrorCode.INSUFFICIENT_STOCK);
        }
    }
}
