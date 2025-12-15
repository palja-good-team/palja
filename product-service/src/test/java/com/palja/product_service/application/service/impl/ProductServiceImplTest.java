package com.palja.product_service.application.service.impl;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.dto.external.CompanyUserInfoRes;
import com.palja.product_service.application.dto.res.CreateProductRes;
import com.palja.product_service.application.dto.res.FindProductListByConditionRes;
import com.palja.product_service.application.dto.res.FindProductRes;
import com.palja.product_service.application.port.UserClient;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.dto.res.FindProductListByConditionDto;
import com.palja.product_service.domain.entity.Category;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.domain.service.ProductCategoryService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DslProductRepository dslProductRepository;

    @Mock
    private UserClient userService;

    @Mock
    private ProductCategoryService productCategoryService;

    private CreateProductCommand createProductCommand;
    private CompanyUserInfoRes companyUserInfo;
    private Product product;

    @BeforeEach
    void init() {
        createProductCommand = new CreateProductCommand(
                "상품", "설명", 1000L, 100, "001001001"
        );

        companyUserInfo = new CompanyUserInfoRes(
                1L, UUID.randomUUID(), "loginId", "password",
                "name", "number", "email", "address",
                "role", "status", LocalDateTime.now(), "loginId", LocalDateTime.now(),
                "loginId"
        );

        product = Product.create(createProductCommand.name(),
                createProductCommand.description(),
                createProductCommand.price(),
                companyUserInfo.getCompanyUserId(),
                companyUserInfo.getCompanyName(),
                createProductCommand.stock());
        product.assignCategory(Category.create(createProductCommand.category()));
    }

    @Test
    @DisplayName("새로운 상품을 생성한다")
    void createProduct() {
        //given
        CreateProductCommand command = createProductCommand;
        Product expected = product;

        given(userService.getMyInfo()).willReturn(companyUserInfo);
        given(productCategoryService.createProductAndCategory(command, companyUserInfo)).willReturn(expected);
        given(productRepository.isNotUnique(anyString(), anyString(), anyString())).willReturn(Boolean.FALSE);
        given(productRepository.save(any(Product.class))).willReturn(expected);

        //when
        CreateProductRes result = productService.createProduct(command);

        //then
        assertThat(result.getName()).isEqualTo(expected.getName());
        assertThat(result.getDescription()).isEqualTo(expected.getDescription());
        assertThat(result.getPrice()).isEqualTo(expected.getPrice().toString());
        assertThat(result.getCategory()).isEqualTo(expected.getCategory().getCategoryNumber());
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
        FindProductListByConditionDto repositoryResultDto = new FindProductListByConditionDto(
                product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getCategory().getCategoryNumber(), product.getAvgRating());

        PageRequest pageRequest = PageRequest.of(0, 10);

        given(productRepository.findProductsToCondition(any(FindListByConditionReq.class), anyLong(), anyInt()))
                .willReturn(List.of(repositoryResultDto, repositoryResultDto));

        //when
        Page<FindProductListByConditionRes> products = productService.findProducts(command, pageRequest);

        //then
        assertThat(products.getTotalElements()).isEqualTo(2);
    }
}