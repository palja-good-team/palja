package com.palja.product_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.common.exception.BusinessException;
import com.palja.product_service.domain.vo.Money;
import com.palja.product_service.exception.ProductErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_product",
        uniqueConstraints = @UniqueConstraint(name = "companyCategoryName",
                columnNames = {"companyName", "categoryId", "name"}))
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

    @Column(scale = 1, precision = 2)
    private BigDecimal avgRating;

    private UUID companyUserId;

    private String companyName;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    private ProductStock productStock;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    protected Product() {}

    public static Product create(String name, String description, Long price, UUID companyUserId, String companyName, Long stock) {

        Product product = new Product();

        product.name = name;
        product.description = description;
        product.price = Money.of(price);
        product.avgRating = BigDecimal.ZERO;
        product.companyName = companyName;
        product.companyUserId = companyUserId;
        product.productStock = new ProductStock(product, stock);

        return product;
    }

    public Product assignCategory(Category category) {
        this.category = category;
        return this;
    }

    public Product updateInfo(String name, String description, Long price, Category category) {

        if(name.length() > 30) throw new BusinessException(ProductErrorCode.NAME_TOO_LONG);
        this.name = name;

        this.description = description;
        this.price = Money.of(price);
        this.category = category;

        return this;
    }

    public Product updateStock(Long stock) {
        if(stock < 0)
            throw new BusinessException(ProductErrorCode.INVALID_STOCK);

        this.productStock.updateQuantity(stock);
        return this;
    }

    public ProductStock increaseStock(Long quantity) {
        this.productStock = productStock.increase(quantity);
        return this.productStock;
    }

    public ProductStock decreaseStock(Long quantity) {
        this.productStock = productStock.decrease(quantity);
        return this.productStock;
    }

    public Money increaseFixPrice(Long amount) {
        this.price = price.plus(amount);
        return this.price;
    }

    public Money increaseRatePrice(Double amount) {
        double rate = 1L + amount;
        this.price = price.multiply(rate);
        return this.price;
    }

    public Money discountFixPrice(Long amount) {
        this.price = price.minus(amount);
        return this.price;
    }

    public Money discountRatePrice(Double amount) {
        double rate = 1L - amount;
        this.price = price.multiply(rate);
        return this.price;
    }

    public void delete() {
        this.productStock.delete();
        super.softDelete();
    }
}
