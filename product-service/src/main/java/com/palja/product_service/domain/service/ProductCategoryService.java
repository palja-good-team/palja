package com.palja.product_service.domain.service;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.dto.external.CompanyUserInfoRes;
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

    public Product createProductAndCategory(CreateProductCommand createCommand, CompanyUserInfoRes myInfo) {

        Product product = Product.create(createCommand.name(),
                createCommand.description(),
                createCommand.price(),
                myInfo.getCompanyUserId(),
                myInfo.getCompanyName(),
                createCommand.stock());

        Optional<Category> optionalCategory = findCategory(createCommand.category());
        if (optionalCategory.isPresent()) {
            return product.assignCategory(optionalCategory.get());
        }

        return product.assignCategory(Category.create(createCommand.category()));
    }

    public Optional<Category> findCategory(String categoryNumber) {

        return categoryRepository.findByCategoryNumber(categoryNumber);
    }
}
