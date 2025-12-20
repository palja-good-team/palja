package com.palja.product_service.application.service.impl;

import com.palja.common.auditor.CurrentUser;
import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.command.UpdateProductInfoCommand;
import com.palja.product_service.application.dto.external.CompanyUserInfoRes;
import com.palja.product_service.application.dto.res.*;
import com.palja.product_service.application.event.ChangePriceEvent;
import com.palja.product_service.application.event.DecreaseStockTimeDealErrorEvent;
import com.palja.product_service.application.event.SaleProductErrorEvent;
import com.palja.product_service.application.port.UserClient;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.dto.res.FindProductListByConditionDto;
import com.palja.product_service.domain.dto.res.ProductInfoForOrderDto;
import com.palja.product_service.domain.dto.res.ProductInfoForTimeDealDto;
import com.palja.product_service.domain.entity.Category;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.entity.ProductStock;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.domain.service.ProductCategoryService;
import com.palja.product_service.exception.CategoryErrorCode;
import com.palja.product_service.exception.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductCategoryService productCategoryService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserClient userClient;

    @Override
    @Transactional
    public CreateProductRes createProduct(CreateProductCommand createCommand) {

        CompanyUserInfoRes myInfo = userClient.getMyInfo();
        Product product = productCategoryService
                .createProductAndCategory(createCommand.toDomainReq(), myInfo.toUserInfo());

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

        Optional<Category> optionalCategory = productCategoryService.findCategory(updateCommand.category());
        if (optionalCategory.isEmpty()) {
            throw new BusinessException(CategoryErrorCode.NOT_FOUND_CATEGORY);
        }

        Long beforePrice = product.getPrice().getAmount();
        Product updateProduct = product.updateInfo(
                updateCommand.name(),
                updateCommand.description(),
                updateCommand.price(),
                optionalCategory.get());
        Long afterPrice = product.getPrice().getAmount();

        if(!beforePrice.equals(afterPrice)) {
            applicationEventPublisher.publishEvent(ChangePriceEvent.create(productId, afterPrice));
        }

        return UpdateProductInfoRes.fromEntity(updateProduct);
    }

    @Override
    @Transactional
    public UpdateStockRes updateStock(UUID productId,
                                      Long stock) {

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
    public void saleProduct(UUID sagaId,
                            UUID productId,
                            Long quantity) {

        ProductStock stock = repository.findProductStock(productId);

        boolean result = repository.decreaseStockBySale(
                productId.toString(), stock.getQuantity(), quantity);
        validateRedisOperation(result);

        applicationEventPublisher.publishEvent(
                SaleProductErrorEvent.create(sagaId, productId));
    }

    @Override
    @Transactional
    public void stockRestore(UUID productId,
                             Long quantity) {

        ProductStock restoredStock = repository.findProduct(productId).increaseStock(quantity);

        boolean result = repository.adjustStock(
                productId.toString(), restoredStock.getQuantity());
        validateRedisOperation(result);

    }

    @Override
    @Transactional
    public void decreaseStockForTimeDeal(UUID productId, Long quantity) {

        ProductStock restoredStock = repository.findProduct(productId).decreaseStock(quantity);

        boolean result = repository.adjustStock(
                productId.toString(), restoredStock.getQuantity());
        validateRedisOperation(result);

        applicationEventPublisher.publishEvent(
                DecreaseStockTimeDealErrorEvent.create(
                        productId, ProductErrorCode.INVALID_STOCK.getMessage()
        ));
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productId) {

        Product product = repository.findProduct(productId);
        CompanyUserInfoRes myInfo = userClient.getMyInfo();

        validateIsSameUser(product.getCompanyUserId(), myInfo.getCompanyUserId());

        product.softDelete();

        boolean result = repository.deleteStockFromRedis(productId.toString());
        validateRedisOperation(result);
    }

    @Override
    @Transactional
    public void deleteProductForUser(UUID companyUserId) {

        String loginId = CurrentUser.getLoginId();
        repository.deleteProductForUser(companyUserId, loginId);

        List<UUID> idList = repository.findAllIdsByCompanyUserId(companyUserId);
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
