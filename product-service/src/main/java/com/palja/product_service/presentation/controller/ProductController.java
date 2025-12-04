package com.palja.product_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.command.UpdateProductCommand;
import com.palja.product_service.application.dto.res.*;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.presentation.dto.req.CreateProductReq;
import com.palja.product_service.presentation.dto.req.FindProductListByConditionReq;
import com.palja.product_service.presentation.dto.req.UpdateProductReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService service;

    @PostMapping()
    public ResponseEntity<ApiResponse<CreateProductRes>> createProduct(@RequestBody @Valid CreateProductReq req) {

        CreateProductCommand createCommand = req.toCommand(req);
        CreateProductRes res = service.createProduct(createCommand);

        return new ResponseEntity<>(ApiResponse.success(res, "상품 등록 성공"), HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<FindProductRes>> findProduct(@PathVariable UUID productId) {

        FindProductRes res = service.findProduct(productId);
        return new ResponseEntity<>(ApiResponse.success(res, "상품 조회 성공"), HttpStatus.OK);
    }

    @GetMapping("/condition")
    public ResponseEntity<PageResponse<FindProductListByConditionRes>> findProducts(@RequestBody @Valid FindProductListByConditionReq req,
                                                                                    Pageable pageable) {

        FindProductListByConditionCommand command = req.toCommand();
        Page<FindProductListByConditionRes> res = service.findProducts(command, pageable);

        return new ResponseEntity<>(PageResponse.from(res), HttpStatus.OK);
    }

    @GetMapping("/time-deal/{productId}")
    public ResponseEntity<ApiResponse<ProductInfoForTimeDealRes>> provideProductInfoToTimeDeal(@PathVariable UUID productId) {

        ProductInfoForTimeDealRes res = service.findProductForTimeDeal(productId);

        return new ResponseEntity<>(ApiResponse.success(res, "상품 정보 조회 성공"), HttpStatus.OK);
    }

    @PutMapping("/manager/{productId}")
    public ResponseEntity<ApiResponse<UpdateProductRes>> updateProduct(@RequestBody @Valid UpdateProductReq req,
                                                                       @PathVariable UUID productId) {

        UpdateProductCommand command = req.toCommand();
        UpdateProductRes res = service.updateProduct(productId, command);

        return new ResponseEntity<>(ApiResponse.success(res, "상품 정보 수정 성공"), HttpStatus.OK);
    }
}
