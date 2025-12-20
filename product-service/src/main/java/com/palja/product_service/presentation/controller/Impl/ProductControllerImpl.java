package com.palja.product_service.presentation.controller.Impl;

import com.palja.common.annotation.RequiredInternal;
import com.palja.common.annotation.RequiredRole;
import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.command.UpdateProductInfoCommand;
import com.palja.product_service.application.dto.res.*;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.presentation.controller.ProductController;
import com.palja.product_service.presentation.dto.req.CreateProductReq;
import com.palja.product_service.presentation.dto.req.FindProductListByConditionReq;
import com.palja.product_service.presentation.dto.req.UpdateProductInfoReq;
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
public class ProductControllerImpl implements ProductController {

    private final ProductService service;

    @RequiredRole(UserRole.COMPANY_USER)
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

    @RequiredInternal
    @GetMapping("/time-deal/{productId}")
    public ResponseEntity<ApiResponse<ProductInfoForTimeDealRes>> provideProductInfoToTimeDeal(@PathVariable UUID productId) {

        ProductInfoForTimeDealRes res = service.findProductForTimeDeal(productId);

        return new ResponseEntity<>(ApiResponse.success(res, "상품 정보 조회 성공"), HttpStatus.OK);
    }

    @RequiredInternal
    @GetMapping("/order/{productId}")
    public ResponseEntity<ApiResponse<ProductInfoForOrderRes>> provideProductInfoToOrder(@PathVariable UUID productId) {

        ProductInfoForOrderRes res = service.findProductForOrder(productId);

        return new ResponseEntity<>(ApiResponse.success(res, "상품 정보 조회 성공"), HttpStatus.OK);
    }

    @RequiredRole(UserRole.COMPANY_USER)
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<UpdateProductInfoRes>> updateProductInfo(@RequestBody @Valid UpdateProductInfoReq req,
                                                                               @PathVariable UUID productId) {

        UpdateProductInfoCommand command = req.toCommand();
        UpdateProductInfoRes res = service.updateProductInfo(productId, command);

        return new ResponseEntity<>(ApiResponse.success(res, "상품 정보 수정 성공"), HttpStatus.OK);
    }

    @RequiredRole(UserRole.COMPANY_USER)
    @PutMapping("/modifyStock/{productId}")
    public ResponseEntity<ApiResponse<UpdateStockRes>> updateProductStock(@PathVariable UUID productId,
                                                                          @RequestParam Long stock) {

        UpdateStockRes res = service.updateStock(productId, stock);

        return new ResponseEntity<>(ApiResponse.success(res, "상품 재고 수정 성공"), HttpStatus.OK);
    }

    @RequiredRole(UserRole.COMPANY_USER)
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable UUID productId) {

        service.deleteProduct(productId);
        return new ResponseEntity<>(ApiResponse.success("상품 삭제 성공"), HttpStatus.OK);
    }

    @RequiredInternal
    @DeleteMapping("/user/{companyUserId}")
    public ResponseEntity<ApiResponse<String>> deleteProductForUser(@PathVariable UUID companyUserId) {

        service.deleteProductForUser(companyUserId);
        return new ResponseEntity<>(ApiResponse.success("상품 삭제 성공"), HttpStatus.OK);
    }
}
