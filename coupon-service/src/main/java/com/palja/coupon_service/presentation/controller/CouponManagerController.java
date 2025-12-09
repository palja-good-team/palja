package com.palja.coupon_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.coupon_service.application.dto.coupon.*;
import com.palja.coupon_service.presentation.dto.request.ChangeCouponStatusReq;
import com.palja.coupon_service.presentation.dto.request.CreateCouponReq;
import com.palja.coupon_service.presentation.dto.request.UpdateCouponReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "관리자 쿠폰 관리", description = "관리자 쿠폰 생성, 수정, 상태 변경, 조회 API")
public interface CouponManagerController {

    @Operation(
            summary = "쿠폰 생성",
            description = "쿠폰을 생성합니다."
    )
    ResponseEntity<ApiResponse<CreateCouponRes>> createCoupon(@Valid @RequestBody CreateCouponReq request);

    @Operation(
            summary = "쿠폰 수정",
            description = "쿠폰을 수정합니다."
    )
    ResponseEntity<ApiResponse<UpdateCouponRes>> updateCoupon(@PathVariable UUID couponId, @Valid @RequestBody UpdateCouponReq request);

    @Operation(
            summary = "쿠폰 상태",
            description = "쿠폰 상태를 변경합니다."
    )
    ResponseEntity<ApiResponse<ChangeStatusCouponRes>> changeCouponStatus(@PathVariable UUID couponId,
                                                                          @Valid @RequestBody ChangeCouponStatusReq request);

    @Operation(
            summary = "쿠폰 목록 조회",
            description = "생성된 쿠폰 목록을 조회합니다."
    )
    ResponseEntity<ApiResponse<PageResponse<ReadCouponRes>>> getCouponList(Pageable pageable);

    @Operation(
            summary = "쿠폰 상세 조회",
            description = "쿠폰을 상세 조회합니다."
    )
    ResponseEntity<ApiResponse<ReadCouponDetailRes>> getCouponDetail(@PathVariable UUID couponId);
}
