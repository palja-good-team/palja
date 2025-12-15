package com.palja.product_service.domain.repository;

import com.palja.product_service.domain.entity.Category;

import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findByCategoryNumber(String number);
}
