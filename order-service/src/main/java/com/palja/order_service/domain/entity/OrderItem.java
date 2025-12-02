package com.palja.order_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "time_deal_id")
    private UUID timeDealId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "unit_price", nullable = false)
    private Long unitPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "line_total_amount", nullable = false)
    private Long lineTotalAmount;

    @Column(name = "time_deal_price")
    private Long timeDealPrice;

    @Column(name = "time_deal_discount_amount")
    private Long timeDealDiscountAmount;
}