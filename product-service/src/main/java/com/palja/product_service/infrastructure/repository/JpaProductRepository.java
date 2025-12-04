package com.palja.product_service.infrastructure.repository;

import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.vo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<Product, UUID> {

    @Query(value = "SELECT p FROM Product p INNER JOIN FETCH p.productStock WHERE p.id = :productId")
    Optional<Product> findByIdFetchStock(@Param("productId") UUID productId);

    Boolean existsByCompanyNameAndCategoryAndName(String companyName, Category category, String name);
}
