package com.palja.product_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.dto.CreateProductRes;
import com.palja.product_service.application.dto.FindProductRes;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.presentation.dto.req.CreateProductReq;
import com.palja.product_service.presentation.dto.res.ProductCreateRes;
import com.palja.product_service.presentation.dto.res.ProductFindRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService service;

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductCreateRes>> createProduct(@RequestBody @Valid CreateProductReq createReq) {

        CreateProductCommand createCommand = createReq.toCommand(createReq);
        CreateProductRes productRes = service.createProduct(createCommand);

        ProductCreateRes productDetail = ProductCreateRes.fromRes(productRes);
        return new ResponseEntity<>(ApiResponse.success(productDetail,"상품 등록 성공"), HttpStatus.CREATED);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductFindRes>> findProduct(@PathVariable UUID productId) {

        FindProductRes res = service.getProduct(productId);
        ProductFindRes productFindRes = ProductFindRes.fromRes(res);
        return new ResponseEntity<>(ApiResponse.success(productFindRes,"상품 조회 성공"), HttpStatus.OK);
    }
}
