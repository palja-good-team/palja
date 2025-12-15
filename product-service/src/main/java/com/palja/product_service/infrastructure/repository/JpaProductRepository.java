package com.palja.product_service.infrastructure.repository;

import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.entity.ProductStock;
import com.palja.product_service.domain.vo.Category;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p JOIN FETCH p.productStock WHERE p.id = :productId AND p.deletedAt IS null")
    Optional<Product> findByIdFetchStock(@Param("productId") UUID productId);

    @Query("SELECT ps FROM ProductStock ps JOIN Product p ON ps.id = :productId AND p.deletedAt IS NULL")
    Optional<ProductStock> findStockByProductId(@Param("productId") UUID productId);

    Boolean existsByCompanyNameAndCategoryAndNameAndDeletedAtIsNull(String companyName, Category category, String name);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p JOIN FETCH p.productStock WHERE p.id = :productId AND p.deletedAt IS null")
    Optional<Product> findByIdFetchStockWithLock(@Param("productId") UUID productId);

    @Query("SELECT p.id FROM Product p WHERE p.companyUserId = :companyUserId")
    List<UUID> findAllIdsByCompanyUserId(@Param("companyUserId")UUID companyUserId);

    @Modifying
    @Query("UPDATE Product p SET p.deletedAt = local datetime WHERE p.companyUserId = :companyUserId")
    void deleteAllByCompanyUserId(@Param("companyUserId") UUID companyUserId);
}
