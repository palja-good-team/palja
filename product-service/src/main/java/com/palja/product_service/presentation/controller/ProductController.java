package com.palja.product_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.dto.res.CreateProductRes;
import com.palja.product_service.application.dto.res.FindProductListByConditionRes;
import com.palja.product_service.application.dto.res.FindProductRes;
import com.palja.product_service.application.dto.res.ProductInfoForTimeDealRes;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.presentation.dto.req.CreateProductReq;
import com.palja.product_service.presentation.dto.req.FindProductListByConditionReq;
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
    public ResponseEntity<ApiResponse<CreateProductRes>> createProduct(@RequestBody @Valid CreateProductReq createReq) {

        CreateProductCommand createCommand = createReq.toCommand(createReq);
        CreateProductRes res = service.createProduct(createCommand);

        return new ResponseEntity<>(ApiResponse.success(res,"상품 등록 성공"), HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<FindProductRes>> findProduct(@PathVariable UUID productId) {

        FindProductRes res = service.findProduct(productId);
        return new ResponseEntity<>(ApiResponse.success(res,"상품 조회 성공"), HttpStatus.OK);
    }

    @GetMapping("/condition")
    public ResponseEntity<PageResponse<FindProductListByConditionRes>> findProducts(@RequestBody @Valid FindProductListByConditionReq req,
                                                                                    Pageable pageable) {

        FindProductListByConditionCommand command = req.toCommand();
        Page<FindProductListByConditionRes> res = service.findProducts(command, pageable);

        return new ResponseEntity<>(PageResponse.from(res), HttpStatus.OK);
    }

    @GetMapping("/timedeal/{productId}")
    public ResponseEntity<ApiResponse<ProductInfoForTimeDealRes>> provideProductInfoToTimeDeal(@PathVariable UUID productId) {

        ProductInfoForTimeDealRes res = service.findProductForTimeDeal(productId);

        return new ResponseEntity<>(ApiResponse.success(res, "상품 정보 조회 성공"), HttpStatus.OK);
    }
}
