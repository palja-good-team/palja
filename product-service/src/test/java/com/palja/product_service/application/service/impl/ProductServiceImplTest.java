package com.palja.product_service.application.service.impl;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.dto.CreateProductRes;
import com.palja.product_service.application.dto.FindProductRes;
import com.palja.product_service.application.dto.ProductListByConditionRes;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.infrastructure.repository.DslProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DslProductRepository dslProductRepository;

    private CreateProductCommand createProductCommand;
    private Product product;

    @BeforeEach
    void init() {
        createProductCommand = new CreateProductCommand(
                "상품", "설명", 1000L, 100, "FOOD", "회사이름"
        );

        product = Product.create(createProductCommand.name(),
                createProductCommand.description(),
                createProductCommand.price(),
                createProductCommand.category(),
                UUID.randomUUID(),
                createProductCommand.companyName(),
                createProductCommand.stock());
    }

    @Test
    @DisplayName("새로운 상품을 생성한다")
    void createProduct() {
        //given
        CreateProductCommand command = createProductCommand;
        Product expected = product;

        given(productRepository.isNotUnique(any(Product.class))).willReturn(Boolean.FALSE);
        given(productRepository.save(any(Product.class))).willReturn(expected);

        //when
        CreateProductRes result = productService.createProduct(command);

        //then
        assertThat(result.getName()).isEqualTo(expected.getName());
        assertThat(result.getDescription()).isEqualTo(expected.getDescription());
        assertThat(result.getPrice()).isEqualTo(expected.getPrice());
        assertThat(result.getCategory()).isEqualTo(expected.getCategory().name());
        assertThat(result.getCompanyName()).isEqualTo(expected.getCompanyName());
    }

    @Test
    @DisplayName("상품 단건조회에 성공한다")
    void findProduct() {
        //given
        FindProductRes expected = FindProductRes.fromEntity(product);

        given(productRepository.findProduct(product.getId())).willReturn(product);

        //when
        FindProductRes result = productService.findProduct(product.getId());

        //then
        assertThat(result.getProductId()).isEqualTo(expected.getProductId());
        assertThat(result.getName()).isEqualTo(expected.getName());
        assertThat(result.getDescription()).isEqualTo(expected.getDescription());
        assertThat(result.getPrice()).isEqualTo(expected.getPrice());
        assertThat(result.getCategory()).isEqualTo(expected.getCategory());
        assertThat(result.getAvgRating()).isEqualTo(expected.getAvgRating());
    }
    @Test
    @DisplayName("조건에 따른 상품목록 조회에 성공한다")
    void findProducts() {
        //given
        FindProductListByConditionCommand command = new FindProductListByConditionCommand(
                createProductCommand.name(),
                100L, 10000L, "food", BigDecimal.ZERO, BigDecimal.valueOf(5.0)
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        given(productRepository.findProductsToCondition(any(FindListByConditionReq.class), any(Pageable.class)))
                .willReturn(List.of(product,product));

        //when
        Page<ProductListByConditionRes> products = productService.findProducts(command, pageRequest);

        //then
        assertThat(products.getTotalElements()).isEqualTo(2);
    }
}