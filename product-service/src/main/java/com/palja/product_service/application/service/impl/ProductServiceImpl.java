package com.palja.product_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.dto.res.CreateProductRes;
import com.palja.product_service.application.dto.res.FindProductRes;
import com.palja.product_service.application.dto.res.FindProductListByConditionRes;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.domain.vo.Category;
import com.palja.product_service.infrastructure.repository.DslProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final DslProductRepository dslProductRepository;

    @Override
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
        if(repository.isNotUnique(product))
            throw new BusinessException(CommonErrorCode.BAD_REQUEST);

        Product savedProduct = repository.save(product);

        return CreateProductRes.fromEntity(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public FindProductRes findProduct(UUID productId) {

        Product product = repository.findProduct(productId);
        return FindProductRes.fromEntity(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FindProductListByConditionRes> findProducts(FindProductListByConditionCommand command, Pageable pageable) {

        FindListByConditionReq req = new FindListByConditionReq(
                command.getName(),
                command.getMinPrice(),
                command.getMaxPrice(),
                Category.fromString(command.getCategory()),
                command.getMinRating(),
                command.getMaxRating()
        );

        List<Product> productList = repository.findProductsToCondition(req, pageable);
        Long pageCount = dslProductRepository.getPageCount(req);

        List<FindProductListByConditionRes> content =
                productList.stream().map(FindProductListByConditionRes::fromEntity).toList();

        return new PageImpl<>(content,pageable,pageCount);
    }
}
