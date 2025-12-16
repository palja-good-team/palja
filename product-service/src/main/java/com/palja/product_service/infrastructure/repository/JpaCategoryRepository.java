package com.palja.product_service.infrastructure.repository;

import com.palja.product_service.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaCategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByCategoryNumber(String categoryNumber);
}
