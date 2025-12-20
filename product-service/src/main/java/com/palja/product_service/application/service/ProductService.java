package com.palja.product_service.application.service;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.command.UpdateProductInfoCommand;
import com.palja.product_service.application.dto.res.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    CreateProductRes createProduct(CreateProductCommand createCommand);

    FindProductRes findProduct(UUID productId);

    Page<FindProductListByConditionRes> findProducts(FindProductListByConditionCommand command, Pageable pageable);

    ProductInfoForTimeDealRes findProductForTimeDeal(UUID productId);

    ProductInfoForOrderRes findProductForOrder(UUID productId);

    UpdateProductInfoRes updateProductInfo(UUID productId, UpdateProductInfoCommand updateCommand);

    UpdateStockRes updateStock(UUID productId, Long stock);

    SaleProductRes saleProduct(UUID productId, Long quantity);

    RestoreStockRes stockRestore(UUID productId, Long quantity);

    void deleteProduct(UUID productId);

    void deleteProductForUser(UUID companyUserId);
}
