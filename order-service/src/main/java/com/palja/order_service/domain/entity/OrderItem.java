package com.palja.order_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id", nullable = false, updatable = false)
    private UUID orderItemId;

    // FK owner (JPA 연관관계 주인)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "time_deal_id")
    private UUID timeDealId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "line_total_amount", nullable = false)
    private BigDecimal lineTotalAmount;

    @Column(name = "time_deal_price")
    private BigDecimal timeDealPrice;

    @Column(name = "time_deal_discount_amount")
    private BigDecimal timeDealDiscountAmount;

     // OrderItem 생성
    public static OrderItem create(
            Order order,
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            Long quantity,
            UUID timeDealId,
            BigDecimal timeDealPrice
    ) {
        validateQuantity(quantity);
        validateUnitPrice(unitPrice);
        validateProductName(productName);

        // 자동 계산
        BigDecimal lineTotalAmount = calculateLineTotalAmount(unitPrice, quantity);
        BigDecimal timeDealDiscountAmount = calculateTimeDealDiscount(unitPrice, timeDealPrice, quantity);

        return OrderItem.builder()
                .order(order)
                .productId(productId)
                .productName(productName)
                .unitPrice(unitPrice)
                .quantity(quantity)
                .timeDealId(timeDealId)
                .timeDealPrice(timeDealPrice)
                .lineTotalAmount(lineTotalAmount)
                .timeDealDiscountAmount(timeDealDiscountAmount)
                .build();
    }

    // 할인 전 총 금액 계산
    private static BigDecimal calculateLineTotalAmount(BigDecimal unitPrice, Long quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // 타임딜 할인 금액 계산
    private static BigDecimal calculateTimeDealDiscount(
            BigDecimal unitPrice,
            BigDecimal timeDealPrice,
            Long quantity
    ) {
        if (timeDealPrice == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountPerUnit = unitPrice.subtract(timeDealPrice);

        if (discountPerUnit.compareTo(BigDecimal.ZERO) <= 0) {
            // 타임딜 가격이 정상이거나 더 비싸다면 할인 없음
            return BigDecimal.ZERO;
        }

        return discountPerUnit.multiply(BigDecimal.valueOf(quantity));
    }

    // 타임딜 주문 여부
    public boolean isTimeDeal() {
        return timeDealId != null;
    }

    // ====== Validation ======
    private static void validateQuantity(Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
    }

    private static void validateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("상품 단가는 0보다 커야 합니다.");
        }
    }

    private static void validateProductName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (productName.length() > 200) {
            throw new IllegalArgumentException("상품명은 200자를 초과할 수 없습니다.");
        }
    }
}