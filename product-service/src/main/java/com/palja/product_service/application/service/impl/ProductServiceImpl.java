package com.palja.product_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.command.UpdateProductInfoCommand;
import com.palja.product_service.application.dto.res.*;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.entity.ProductStock;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.domain.repository.RedisRepository;
import com.palja.product_service.domain.vo.Category;
import com.palja.product_service.exception.ProductErrorCode;
import com.palja.product_service.infrastructure.dto.CompanyUserInfoDto;
import com.palja.product_service.application.service.UserService;
import com.palja.product_service.infrastructure.repository.DslProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final DslProductRepository dslProductRepository;
    private final RedisRepository redisRepository;
    private final UserService userClient;

    @Value("${redis-key.map0}")
    private String map0Key;

    @Value("${redis-key.map1}")
    private String map1Key;


    @Override
    @Transactional
    public CreateProductRes createProduct(CreateProductCommand createCommand) {

        CompanyUserInfoDto myInfo = userClient.getMyInfo();

        Product product = Product.create(createCommand.name(),
                createCommand.description(),
                createCommand.price(),
                createCommand.category(),
                myInfo.getCompanyUserId(),
                myInfo.getCompanyName(),
                createCommand.stock());

        if(repository.isNotUnique(product.getCompanyName(), product.getCategory(), product.getName()))
            throw new BusinessException(ProductErrorCode.DUPLICATE_PRODUCT);

        Product savedProduct = repository.save(product);

        return CreateProductRes.fromEntity(savedProduct);
    }

    @Override
    public FindProductRes findProduct(UUID productId) {

        Product product = repository.findProduct(productId);
        return FindProductRes.fromEntity(product);
    }

    @Override
    public Page<FindProductListByConditionRes> findProducts(FindProductListByConditionCommand command, Pageable pageable) {

        FindListByConditionReq req = new FindListByConditionReq(
                command.name(),
                command.minPrice(),
                command.maxPrice(),
                Category.fromString(command.category()),
                command.minRating(),
                command.maxRating()
        );

        List<Product> productList = repository.findProductsToCondition(req, pageable);
        Long pageCount = dslProductRepository.getPageCount(req);

        List<FindProductListByConditionRes> content =
                productList.stream().map(FindProductListByConditionRes::fromEntity).toList();

        return new PageImpl<>(content,pageable,pageCount);
    }

    @Override
    public ProductInfoForTimeDealRes findProductForTimeDeal(UUID productId) {
        return Optional.ofNullable(
                        dslProductRepository.findProductForTimeDeal(productId))
                .orElseThrow(
                        () -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
                );
    }

    @Override
    public ProductInfoForOrderRes findProductForOrder(UUID productId) {
        return Optional.ofNullable(
                        dslProductRepository.findProductForOrder(productId))
                .orElseThrow(
                        () -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
                );
    }

    @Override
    @Transactional
    public UpdateProductInfoRes updateProductInfo(UUID productId, UpdateProductInfoCommand updateCommand) {

        Product product = repository.findProduct(productId);
        CompanyUserInfoDto myInfo = userClient.getMyInfo();

        if (isDifferCompanyUser(product.getCompanyUserId(), myInfo.getCompanyUserId())) {
            throw new BusinessException(ProductErrorCode.FORBIDDEN_REQUEST);
        }

        if(repository.isNotUnique(
                product.getCompanyName(),
                Category.fromString(updateCommand.category()),
                updateCommand.name()))
            throw new BusinessException(ProductErrorCode.DUPLICATE_PRODUCT);

        Product updateProduct = product.updateInfo(updateCommand.name(),
                updateCommand.description(),
                updateCommand.price(),
                updateCommand.category());

        return UpdateProductInfoRes.fromEntity(updateProduct);
    }

    @Override
    @Transactional
    public UpdateStockRes updateStock(UUID productId, Integer stock) {

        Product product = repository.findProduct(productId);
        CompanyUserInfoDto myInfo = userClient.getMyInfo();

        if (isDifferCompanyUser(product.getCompanyUserId(), myInfo.getCompanyUserId())) {
            throw new BusinessException(ProductErrorCode.FORBIDDEN_REQUEST);
        }

        Product updateProduct = product.updateStock(stock);

        return UpdateStockRes.fromEntity(updateProduct);
    }

    @Override
    public SaleProductRes saleProduct(UUID productId, Integer quantity) {

        String hashKey = createRedisHashKey(productId);

        Product product = repository.findProduct(productId);
        Integer stock = product.getProductStock().getQuantity();

        boolean result = redisRepository.decreaseStockBySale(hashKey, productId.toString(), stock, quantity);
        validateRedisOperation(result);

        return new SaleProductRes(productId, Boolean.TRUE);
    }

    @Override
    @Transactional
    public RestoreStockRes stockRestore(UUID productId, Integer quantity) {

        ProductStock restoredStock = repository.findProduct(productId).increaseStock(quantity);

        String hashKey = createRedisHashKey(productId);
        boolean result = redisRepository.adjustStock(
                hashKey, productId.toString(), restoredStock.getQuantity());
        validateRedisOperation(result);

        return new RestoreStockRes(productId, Boolean.TRUE);
    }

    @Override
    @Transactional
    public DecreaseStockForTimeDealRes decreaseStockForTimeDeal(UUID productId, Integer quantity) {

        ProductStock decreasedStock = repository.findProduct(productId).decreaseStock(quantity);

        String hashKey = createRedisHashKey(productId);
        boolean result = redisRepository.adjustStock(
                hashKey, productId.toString(), decreasedStock.getQuantity());
        validateRedisOperation(result);

        return new DecreaseStockForTimeDealRes(productId, Boolean.TRUE);
    }

    @Override
    @Transactional
    public IncreaseStockForTimeDealRes increaseStockForTimeDeal(UUID productId, Integer quantity) {

        ProductStock increasedStock = repository.findProduct(productId).increaseStock(quantity);

        String hashKey = createRedisHashKey(productId);
        boolean result = redisRepository.adjustStock(
                hashKey, productId.toString(), increasedStock.getQuantity());
        validateRedisOperation(result);

        return new IncreaseStockForTimeDealRes(productId, Boolean.TRUE);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productId) {

        Product product = repository.findProduct(productId);
        CompanyUserInfoDto myInfo = userClient.getMyInfo();

        if (isDifferCompanyUser(product.getCompanyUserId(), myInfo.getCompanyUserId())) {
            throw new BusinessException(ProductErrorCode.FORBIDDEN_REQUEST);
        }

        product.delete();

        boolean result = redisRepository.deleteProductStock(createRedisHashKey(productId), productId.toString());
        validateRedisOperation(result);
    }

    private boolean isDifferCompanyUser(UUID productCompanyUserId, UUID myId) {
        return !productCompanyUserId.equals(myId);
    }

    private String createRedisHashKey(UUID productId) {

        String substring = productId.toString().substring(0, 8);
        int hash = substring.hashCode();
        if(hash % 2 == 0)
            return map0Key;
        else return map1Key;
    }

    private void validateRedisOperation(boolean redisResult) {
        if (!redisResult) {
            throw new BusinessException(ProductErrorCode.CONNECTION_ERROR_REDIS);
        }
    }
}
