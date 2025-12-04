package com.palja.product_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.common.exception.BusinessException;
import com.palja.product_service.domain.vo.Category;
import com.palja.product_service.domain.vo.Money;
import com.palja.product_service.exception.ProductErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "p_product",
        uniqueConstraints = @UniqueConstraint(name = "companyCategoryName",
                columnNames = {"companyName", "category", "name"}))
@Getter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Embedded
    private Money price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(scale = 1, precision = 2)
    private BigDecimal avgRating;

    private UUID companyUserId;

    private String companyName;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    private ProductStock productStock;

    protected Product() {}

    public static Product create(String name, String description, Long price, String category, UUID companyUserId, String companyName, Integer stock) {

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

    public Product updateProduct(String name, String description, Long price, String category) {

        if (Objects.nonNull(name)) {
            if(name.length() <= 30) this.name = name;
            else throw new BusinessException(ProductErrorCode.NAME_TOO_LONG);
        }

        if(Objects.nonNull(description))
            this.description = description;

        this.price = Objects.nonNull(price) ? Money.of(price) : this.price;
        this.category = Objects.nonNull(category) ? Category.fromString(category) : this.category;

        return this;
    }

    public ProductStock increaseStock(Integer quantity) {
        this.productStock = productStock.increase(quantity);
        return this.productStock;
    }

    public ProductStock decreaseStock(Integer quantity) {
        this.productStock = productStock.decrease(quantity);
        return this.productStock;
    }

    public Money increaseFixPrice(Long amount) {
        this.price = price.plus(Money.of(amount));
        return this.price;
    }

    public Money increaseRatePrice(Double amount) {
        BigDecimal rate = BigDecimal.ONE.add(BigDecimal.valueOf(amount));
        this.price = price.multiply(rate);
        return this.price;
    }

    public Money discountFixPrice(Long amount) {
        this.price = price.minus(Money.of(amount));
        return this.price;
    }

    public Money discountRatePrice(Double amount) {
        BigDecimal rate = BigDecimal.ONE.subtract(BigDecimal.valueOf(amount));
        this.price = price.multiply(rate);
        return this.price;
    }
}
