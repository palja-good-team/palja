package com.palja.product_service.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_product")
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private UUID id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 255, nullable = false)
    private String description;

    @Embedded
    private Money price;

    @Enumerated(EnumType.STRING)
    private Category category;

    private BigDecimal avgRating;

    private Long companyUserId;

    private String companyName;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    private ProductStock productStock;

    protected Product() {}

    public static Product create(String name, String description, Double price, String category, Long companyUserId, String companyName, Integer stock) {

        Product product = new Product();

        product.name = name;
        product.description = description;
        product.price = Money.of(price);
        product.category = Category.fromString(category);
        product.avgRating = BigDecimal.ZERO;
        product.companyName = companyName;
        product.companyUserId = companyUserId;
        product.productStock = new ProductStock(product, stock);

        return product;
    }

    public ProductStock increaseStock(Integer quantity) {
        this.productStock = productStock.increase(quantity);
        return this.productStock;
    }

    public ProductStock decreaseStock(Integer quantity) {
        this.productStock = productStock.decrease(quantity);
        return this.productStock;
    }
}
