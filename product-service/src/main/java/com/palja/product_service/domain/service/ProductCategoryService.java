package com.palja.product_service.domain.service;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.dto.external.CompanyUserInfoRes;
import com.palja.product_service.domain.dto.req.CreateReq;
import com.palja.product_service.domain.dto.req.UserInfo;
import com.palja.product_service.domain.entity.Category;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCategoryService {

    private final CategoryRepository categoryRepository;

    public Product createProductAndCategory(CreateReq req, UserInfo myInfo) {

        Product product = Product.create(req.getName(),
                req.getDescription(),
                req.getPrice(),
                myInfo.getCompanyUserId(),
                myInfo.getCompanyName(),
                req.getStock());

        Optional<Category> optionalCategory = findCategory(req.getCategory());
        if (optionalCategory.isPresent()) {
            return product.assignCategory(optionalCategory.get());
        }

        return product.assignCategory(Category.create(req.getCategory()));
    }

    public Optional<Category> findCategory(String categoryNumber) {

        return categoryRepository.findByCategoryNumber(categoryNumber);
    }
}
