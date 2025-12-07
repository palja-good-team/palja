package com.palja.product_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.command.UpdateProductInfoCommand;
import com.palja.product_service.application.dto.res.*;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.domain.repository.RedisRepository;
import com.palja.product_service.domain.vo.Category;
import com.palja.product_service.exception.ProductErrorCode;
import com.palja.product_service.infrastructure.repository.DslProductRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public CreateProductRes createProduct(CreateProductCommand createCommand) {

        /*
          헤더로 로그인 아이디가 넘어와서, 해당 유저의 아이디를 조회해 가져와야함.
          -> UUID.randomUUID() 부분.
          이때, 회사의 이름을 같이 줘서 이 유저의 회사가 맞는지 검증도 같이 요청
          userClient.요청(로그인아이디, 회사이름);
         */
        Product product = Product.create(createCommand.name(),
                createCommand.description(),
                createCommand.price(),
                createCommand.category(),
                UUID.randomUUID(),
                createCommand.companyName(),
                createCommand.stock());

        /*
            유니크 제약조건 검사 - 회사는 같은 카테고리에 같은 이름의 상품을 등록할 수 없다.
        */
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

        /*
            String loginId = CurrentUser.getLoginId();
            이 정보로, 해당 로그인 아이디를 사용하는 유저의 UUID를 가져와서 상품의 UUID와 비교해야함.
            UUID companyUserId = userClient.요청(loginId);
            if(product.getCompanyUserId().equals(companyUserID)) 가 True여야만 다음 로직 진행.
         */
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
        /*
            String loginId = CurrentUser.getLoginId();
            이 정보로, 해당 로그인 아이디를 사용하는 유저의 UUID를 가져와서 상품의 UUID와 비교해야함.
            UUID companyUserId = userClient.요청(loginId);
            if(product.getCompanyUserId().equals(companyUserID)) 가 True여야만 다음 로직 진행.
         */

        Product updateProduct = product.updateStock(stock);

        return UpdateStockRes.fromEntity(updateProduct);
    }

    @Override
    public SaleProductRes saleProduct(UUID productId, Integer quantity) {

        String hashKey;
        String stringKey = productId.toString();
        String substring = stringKey.substring(0, 8);
        int hash = substring.hashCode();
        if(hash % 2 == 0)
            hashKey = "productStock0";
        else hashKey = "productStock1";

        //찾아오는 이유는 레디스에 저장되어있지 않은 상품이라면 해당 상품의 재고가 필요.
        //재고만 찾아오게 리팩터링 필요.
        Product product = repository.findProduct(productId);
        Integer stock = product.getProductStock().getQuantity();

        boolean finish = redisRepository.decreaseStockBySale(hashKey, stringKey, stock, quantity);

        if (!finish) {
            throw new BusinessException(ProductErrorCode.CONNECTION_ERROR_REDIS);
        }

        return new SaleProductRes(productId, Boolean.TRUE);
    }
}
