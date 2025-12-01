package com.palja.product_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.presentation.dto.req.CreateProductReq;
import com.palja.product_service.presentation.dto.res.ProductDetailRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private ProductService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDetailRes>> createProduct(@RequestBody CreateProductReq createReq) {
    }
}
