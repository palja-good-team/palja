package com.palja.product_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.dto.CreateProductRes;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

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

        return new CreateProductRes(savedProduct);
    }
}
