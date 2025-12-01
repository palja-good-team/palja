package com.palja.product_service.application.service.impl;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.presentation.dto.res.ProductDetailRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    @DisplayName("새로운 상품을 생성한다")
    void createProduct() {
        //given
        CreateProductCommand command = new CreateProductCommand(
                "상품", "설명", 1000L, 100, "FOOD", "회사이름"
        );

        Product product = Product.create(command.name(),
                command.description(),
                command.price(),
                command.category(),
                UUID.randomUUID(),
                command.companyName(),
                command.stock());

        given(productRepository.isNotUnique(any(Product.class))).willReturn(Boolean.FALSE);
        given(productRepository.save(any(Product.class))).willReturn(product);

        //when
        ProductDetailRes result = productService.createProduct(command);

        //then
        assertThat(result.getName()).isEqualTo(product.getName());
        assertThat(result.getDescription()).isEqualTo(product.getDescription());
        assertThat(result.getPrice()).isEqualTo(product.getPrice());
        assertThat(result.getCategory()).isEqualTo(product.getCategory().name());
        assertThat(result.getCompanyName()).isEqualTo(product.getCompanyName());
    }
}