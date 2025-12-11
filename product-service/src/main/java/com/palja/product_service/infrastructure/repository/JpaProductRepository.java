package com.palja.product_service.infrastructure.repository;

import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.vo.Category;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p JOIN FETCH p.productStock WHERE p.id = :productId AND p.deletedAt IS null")
    Optional<Product> findByIdFetchStock(@Param("productId") UUID productId);

    Boolean existsByCompanyNameAndCategoryAndNameAndDeletedAtIsNull(String companyName, Category category, String name);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p JOIN FETCH p.productStock WHERE p.id = :productId AND p.deletedAt IS null")
    Optional<Product> findByIdFetchStockWithLock(@Param("productId") UUID productId);
}
