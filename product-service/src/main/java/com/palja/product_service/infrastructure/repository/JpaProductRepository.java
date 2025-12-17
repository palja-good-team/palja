package com.palja.product_service.infrastructure.repository;

import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.entity.ProductStock;
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

    Boolean existsByCompanyNameAndCategory_CategoryNumberAndNameAndDeletedAtIsNull(String companyName, String category, String name);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p JOIN FETCH p.productStock WHERE p.id = :productId AND p.deletedAt IS null")
    Optional<Product> findByIdFetchStockWithLock(@Param("productId") UUID productId);

    @Query("SELECT p FROM Product p WHERE p.companyUserId = :companyUserId AND p.deletedAt IS null")
    List<UUID> findAllIdsByCompanyUserId(@Param("companyUserId")UUID companyUserId);

    @Modifying
    @Query(value = """
           WITH softdelete_product AS (
                      UPDATE palja.palja_product.p_product p
                      SET deleted_at = CURRENT_TIMESTAMP,
                          deleted_by= :loginId
                      WHERE p.company_user_id = :companyUserId
                      RETURNING p.product_id
           )
           UPDATE palja.palja_product.p_product_stock ps
                      SET deleted_at = CURRENT_TIMESTAMP,
                          deleted_by= :loginId
                      FROM softdelete_product sdp
                      WHERE sdp.product_id = ps.product_id
           """, nativeQuery = true)
    void deleteAllByCompanyUserId(@Param("companyUserId") UUID companyUserId, @Param("loginId") String loginId);
}
