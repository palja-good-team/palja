package com.palja.product_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.common.exception.BusinessException;
import com.palja.product_service.exception.ProductErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "p_category")
@Getter
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id")
    private UUID id;

    @Column(name = "category_number")
    @Size(min = 9, max = 9)
    private String categoryNumber;

    protected Category() {
    }

    protected Category(String categoryNumber) {

        boolean isOnlyNumber = categoryNumber.chars().allMatch(Character::isDigit);

        if (!isOnlyNumber) {
            throw new BusinessException(ProductErrorCode.FORBIDDEN_REQUEST);
        }
        this.categoryNumber = categoryNumber;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Category category = (Category) object;
        return Objects.equals(categoryNumber, category.categoryNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(categoryNumber);
    }
}
