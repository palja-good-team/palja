package com.palja.product_service.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "p_product_stock")
@Getter
public class ProductStock {

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
            throw new IllegalArgumentException();
        return this;
    }
}
