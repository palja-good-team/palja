package com.palja.product_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.product_service.application.dto.res.*;
import com.palja.product_service.presentation.dto.req.CreateProductReq;
import com.palja.product_service.presentation.dto.req.FindProductListByConditionReq;
import com.palja.product_service.presentation.dto.req.UpdateProductInfoReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Product-Controller", description = "상품 관련 API")
public interface ProductController {

    @Operation(summary = "상품 생성",
            description = "요청값을 통해 상품을 등록합니다.")
    @PostMapping
    ResponseEntity<ApiResponse<CreateProductRes>> createProduct(@RequestBody @Valid CreateProductReq req);

    @Operation(summary = "상품 조회",
            description = "상품의 ID로 상품 단건을 조회합니다.")
    @GetMapping("/{productId}")
    ResponseEntity<ApiResponse<FindProductRes>> findProduct(@PathVariable UUID productId);

    @Operation(summary = "상품 목록 조회",
            description = "요청한 조건에 부합하는 상품들의 목록을 조회합니다.")
    @GetMapping("/condition")
    ResponseEntity<PageResponse<FindProductListByConditionRes>> findProducts(@RequestBody @Valid FindProductListByConditionReq req,
                                                                             Pageable pageable);

    @Operation(summary = "상품 정보 전달",
            description = "상품의 ID로 상품 단건의 정보를 타임딜 서비스에 제공합니다.")
    @GetMapping("/time-deal/{productId}")
    ResponseEntity<ApiResponse<ProductInfoForTimeDealRes>> provideProductInfoToTimeDeal(@PathVariable UUID productId);

    @Operation(summary = "상품 정보 전달",
            description = "상품의 ID로 상품 단건의 정보를 주문 서비스에 제공합니다.")
    @GetMapping("/order/{productId}")
    ResponseEntity<ApiResponse<ProductInfoForOrderRes>> provideProductInfoToOrder(@PathVariable UUID productId);

    @Operation(summary = "상품 정보 수정",
            description = "상품의 ID에 해당하는 상품을, 요청값으로 수정합니다.")
    @PutMapping("/manager/{productId}")
    ResponseEntity<ApiResponse<UpdateProductInfoRes>> updateProductInfo(@RequestBody @Valid UpdateProductInfoReq req,
                                                                        @PathVariable UUID productId);

    @Operation(summary = "상품 재고 수정",
            description = "상품의 ID에 해당하는 상품의 재고를 요청값으로 수정합니다.")
    @PutMapping("/manager/modifyStock/{productId}")
    ResponseEntity<ApiResponse<UpdateStockRes>> updateProductStock(@PathVariable UUID productId,
                                                                   @RequestParam Integer stock);

    @Operation(summary = "상품 판매 재고 차감",
            description = "주문 서비스의 요청으로, 상품의 ID에 해당하는 상품이 판매되었을 때 판매수량만큼 재고를 차감합니다.")
    @PutMapping("/order/sale/{productId}")
    ResponseEntity<ApiResponse<SaleProductRes>> saleProduct(@PathVariable UUID productId,
                                                            @RequestParam Integer quantity);

    @Operation(summary = "상품 판매 취소 재고 복원",
            description = "주문 서비스의 요청으로, 상품의 ID에 해당하는 상품의 판매가 취소되었을 때 수량만큼 재고를 복원합니다.")
    @PutMapping("/order/cancel/{productId}")
    ResponseEntity<ApiResponse<RestoreStockRes>> restoreStockByCancel(@PathVariable UUID productId,
                                                                      @RequestParam Integer quantity);

    @Operation(summary = "타임딜 생성 재고 차감",
            description = "타임딜 서비스의 요청으로, 상품의 ID에 해당하는 상품의 타임딜을 위한 재고를 차감합니다.")
    @PutMapping("/time-deal/decrease/{productId}")
    ResponseEntity<ApiResponse<DecreaseStockForTimeDealRes>> decreaseStockForTimeDeal(@PathVariable UUID productId,
                                                                                      @RequestParam Integer quantity);

    @Operation(summary = "타임딜 생성 재고 복원",
            description = "타임딜 서비스의 요청으로, 상품의 ID에 해당하는 상품의 재고를 복원합니다.")
    @PutMapping("/time-deal/increase/{productId}")
    ResponseEntity<ApiResponse<IncreaseStockForTimeDealRes>> increaseStockForTimeDeal(@PathVariable UUID productId,
                                                                                      @RequestParam Integer quantity);

    @Operation(summary = "상품 삭제",
            description = "상품 ID에 해당하는 상품의 논리적 삭제를 진행합니다.")
    @DeleteMapping("/manager/{productId}")
    ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable UUID productId);
}
