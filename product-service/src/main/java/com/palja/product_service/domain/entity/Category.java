package com.palja.product_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.common.exception.BusinessException;
import com.palja.product_service.exception.CategoryErrorCode;
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

    @Column(name = "category_number", unique = true, nullable = false)
    @Size(min = 9, max = 9)
    private String categoryNumber;

    protected Category() {
    }

    public static Category create(String categoryNumber) {

        boolean isOnlyNumber = categoryNumber.chars().allMatch(Character::isDigit);

        if (!isOnlyNumber) {
            throw new BusinessException(CategoryErrorCode.INVALID_NUMBER);
        }

        Category category = new Category();
        category.categoryNumber = categoryNumber;

        return category;
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

    @Override
    public void softDelete() {
        super.softDelete();
    }
}
