package com.palja.product_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.common.exception.BusinessException;
import com.palja.product_service.exception.ProductErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "p_product_stock")
@Getter
public class ProductStock extends BaseEntity {

    @Id
    @Column(name = "product_stock_id")
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    protected ProductStock() {}

    protected ProductStock(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    protected ProductStock increase(Integer quantity) {
        this.quantity += quantity;
        return this;
    }

    protected ProductStock decrease(Integer quantity) {
        this.quantity -= quantity;
        if(this.quantity < 0)
            throw new BusinessException(ProductErrorCode.INVALID_PRODUCT_STOCK);
        return this;
    }
}
