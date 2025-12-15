package com.palja.product_service.infrastructure.repository.impl;

import com.palja.product_service.domain.entity.Category;
import com.palja.product_service.domain.repository.CategoryRepository;
import com.palja.product_service.infrastructure.repository.JpaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;

    @Override
    public Category save(Category category) {
        return jpaCategoryRepository.save(category);
    }

    @Override
    public Optional<Category> findByCategoryNumber(String number) {
        return jpaCategoryRepository.findByCategoryNumber(number);
    }
}
