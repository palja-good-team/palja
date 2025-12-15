package com.palja.product_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.command.UpdateProductInfoCommand;
import com.palja.product_service.application.dto.external.CompanyUserInfoRes;
import com.palja.product_service.application.dto.res.*;
import com.palja.product_service.application.port.UserClient;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.dto.res.FindProductListByConditionDto;
import com.palja.product_service.domain.dto.res.ProductInfoForOrderDto;
import com.palja.product_service.domain.dto.res.ProductInfoForTimeDealDto;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.entity.ProductStock;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.exception.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final UserClient userClient;

    @Override
    @Transactional
    public CreateProductRes createProduct(CreateProductCommand createCommand) {

        CompanyUserInfoRes myInfo = userClient.getMyInfo();

        Product product = Product.create(createCommand.name(),
                createCommand.description(),
                createCommand.price(),
                createCommand.category(),
                myInfo.getCompanyUserId(),
                myInfo.getCompanyName(),
                createCommand.stock());

        if(repository.isNotUnique(product.getCompanyName(), product.getCategory().getCategoryNumber(), product.getName()))
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
    public Page<FindProductListByConditionRes> findProducts(FindProductListByConditionCommand command,
                                                            Pageable pageable) {

        FindListByConditionReq req = command.toDomainReq();

        List<FindProductListByConditionDto> findDtos =
                repository.findProductsToCondition(req, pageable.getOffset(), pageable.getPageSize());
        Long pageCount = repository.getPageCount(req);

        List<FindProductListByConditionRes> content =
                findDtos.stream().map(FindProductListByConditionRes::fromDto).toList();

        return new PageImpl<>(content,pageable,pageCount);
    }

    @Override
    public ProductInfoForTimeDealRes findProductForTimeDeal(UUID productId) {

        ProductInfoForTimeDealDto dto = repository.findProductForTimeDeal(productId);

        return ProductInfoForTimeDealRes.fromDto(dto);
    }

    @Override
    public ProductInfoForOrderRes findProductForOrder(UUID productId) {

        ProductInfoForOrderDto dto = repository.findProductForOrder(productId);

        return  ProductInfoForOrderRes.fromDto(dto);
    }

    @Override
    @Transactional
    public UpdateProductInfoRes updateProductInfo(UUID productId,
                                                  UpdateProductInfoCommand updateCommand) {

        Product product = repository.findProduct(productId);
        CompanyUserInfoRes myInfo = userClient.getMyInfo();

        validateIsSameUser(product.getCompanyUserId(), myInfo.getCompanyUserId());

        if(repository.isNotUnique(
                product.getCompanyName(),
                updateCommand.category(),
                updateCommand.name()))
            throw new BusinessException(ProductErrorCode.DUPLICATE_PRODUCT);

        Product updateProduct = product.updateInfo(
                updateCommand.name(),
                updateCommand.description(),
                updateCommand.price(),
                updateCommand.category());

        return UpdateProductInfoRes.fromEntity(updateProduct);
    }

    @Override
    @Transactional
    public UpdateStockRes updateStock(UUID productId,
                                      Integer stock) {

        Product product = repository.findProduct(productId);
        CompanyUserInfoRes myInfo = userClient.getMyInfo();

        validateIsSameUser(product.getCompanyUserId(), myInfo.getCompanyUserId());

        if (Objects.isNull(stock)) {
            throw new BusinessException(CommonErrorCode.BAD_REQUEST);
        }

        Product updateProduct = product.updateStock(stock);

        return UpdateStockRes.fromEntity(updateProduct);
    }

    @Override
    @Transactional
    public SaleProductRes saleProductV1(UUID productId,
                                        Integer quantity) {

        Product product = repository.findByIdFetchStockWithLock(productId, quantity);
        product.decreaseStock(quantity);

        return new SaleProductRes(productId, Boolean.TRUE);
    }

    @Override
    public SaleProductRes saleProduct(UUID productId,
                                      Integer quantity) {

        ProductStock stock = repository.findProductStock(productId);

        boolean result = repository.decreaseStockBySale(
                productId.toString(), stock.getQuantity(), quantity);
        validateRedisOperation(result);

        return new SaleProductRes(productId, Boolean.TRUE);
    }

    @Override
    @Transactional
    public RestoreStockRes stockRestoreV1(UUID productId,
                                          Integer quantity) {

        Product product = repository.findByIdFetchStockWithLock(productId, quantity);
        product.increaseStock(quantity);

        return new RestoreStockRes(productId, Boolean.TRUE);
    }

    @Override
    @Transactional
    public RestoreStockRes stockRestore(UUID productId,
                                        Integer quantity) {

        ProductStock restoredStock = repository.findProduct(productId).increaseStock(quantity);

        boolean result = repository.adjustStock(
                productId.toString(), restoredStock.getQuantity());
        validateRedisOperation(result);

        return new RestoreStockRes(productId, Boolean.TRUE);
    }

    @Override
    @Transactional
    public DecreaseStockForTimeDealRes decreaseStockForTimeDeal(UUID productId,
                                                                Integer quantity) {

        Product product = repository.findProduct(productId);
        product.decreaseStock(quantity);

        boolean result = repository.adjustStock(
                productId.toString(), product.getProductStock().getQuantity());
        validateRedisOperation(result);

        return new DecreaseStockForTimeDealRes(productId, Boolean.TRUE);
    }

    @Override
    @Transactional
    public IncreaseStockForTimeDealRes increaseStockForTimeDeal(UUID productId,
                                                                Integer quantity) {

        Product product = repository.findProduct(productId);
        product.increaseStock(quantity);

        boolean result = repository.adjustStock(
                productId.toString(), product.getProductStock().getQuantity());
        validateRedisOperation(result);

        return new IncreaseStockForTimeDealRes(productId, Boolean.TRUE);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productId) {

        Product product = repository.findProduct(productId);
        CompanyUserInfoRes myInfo = userClient.getMyInfo();

        validateIsSameUser(product.getCompanyUserId(), myInfo.getCompanyUserId());

        product.delete();

        boolean result = repository.deleteStockFromRedis(productId.toString());
        validateRedisOperation(result);
    }

    @Override
    @Transactional
    public void deleteProductForUser(UUID companyUserId) {

        List<UUID> idList = repository.findAllIdsByCompanyUserId(companyUserId);
        repository.deleteProductForUser(companyUserId);
        boolean result = repository.deleteAllStockFromRedis(idList);
        validateRedisOperation(result);

    }

    private boolean validateIsSameUser(UUID productCompanyUserId,
                                       UUID myId) {

        if(!productCompanyUserId.equals(myId)) {
            throw new BusinessException(ProductErrorCode.FORBIDDEN_REQUEST);
        }
        return true;
    }

    private void validateRedisOperation(boolean redisResult) {
        if (!redisResult) {
            throw new BusinessException(ProductErrorCode.CONNECTION_ERROR_REDIS);
        }
    }
}
