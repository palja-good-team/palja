package com.palja.product_service.domain.repository;

import com.palja.product_service.domain.dto.req.StockScheduleDto;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.vo.Category;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Boolean isNotUnique(String companyName, Category category, String name);

    Product findProduct(UUID productId);

    List<Product> findProductsToCondition(FindListByConditionReq condition, Pageable pageable);

    void stockBulkUpdateForSchedule(Collection<StockScheduleDto> dtos);

    void restoreStock(UUID productId, Integer quantity);
}
