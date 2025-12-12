package com.palja.coupon_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.coupon_service.application.dto.couponUser.*;
import com.palja.coupon_service.presentation.dto.request.ChangeCouponStatusReq;
import com.palja.coupon_service.presentation.dto.request.UseCouponReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "쿠폰 관리", description = "사용자 쿠폰 발급, 수정, 취소, 상태 변경, 삭제, 조회 API")
public interface CouponController {

    @Operation(
            summary = "쿠폰 발급",
            description = "사용자의 쿠폰을 발급합니다."
    )
    ResponseEntity<ApiResponse<CreateCouponUserRes>> issueCoupon(@PathVariable UUID couponId);

    @Operation(
            summary = "선착순 쿠폰 발급",
            description = "사용자의 선착순 쿠폰을 발급합니다."
    )
    ResponseEntity<ApiResponse<CreateCouponUserRes>> issueFirstComeCoupon(@PathVariable UUID couponId);

    @Operation(
            summary = "쿠폰 사용",
            description = "사용자의 쿠폰을 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse
    })
    ResponseEntity<ApiResponse<UsedCouponUserRes>> useCoupon(@PathVariable UUID couponUserId,
                                                             @Valid @RequestBody UseCouponReq useCouponReq);

    @Operation(
            summary = "쿠폰 취소",
            description = "사용자의 쿠폰을 취소합니다."
    )
    ResponseEntity<ApiResponse<CancelCouponUserRes>> cancelCoupon(@PathVariable UUID couponUserId);

    @Operation(
            summary = "쿠폰 상태 변경",
            description = "사용자의 쿠폰 상태를 변경합니다."
    )
    ResponseEntity<ApiResponse<ChangeStatusCouponUserRes>> changeCouponStatus(@PathVariable UUID couponUserId,
                                                                              @Valid @RequestBody ChangeCouponStatusReq request);

    @Operation(
            summary = "쿠폰 삭제",
            description = "사용자의 쿠폰을 삭제합니다."
    )
    ResponseEntity<ApiResponse<DeleteCouponUserRes>> deleteCoupon(@PathVariable UUID couponUserId);

    @Operation(
            summary = "쿠폰 목록 조회",
            description = "사용자의 보유 쿠폰 목록을 조회합니다."
    )
    ResponseEntity<ApiResponse<PageResponse<ReadCouponUserRes>>> getCouponList(@ParameterObject
                                                                               @PageableDefault(
                                                                                       size = 20,
                                                                                       sort = "createdAt",
                                                                                       direction = Sort.Direction.DESC)
                                                                               Pageable pageable);

    @Operation(
            summary = "쿠폰 상세 조회",
            description = "사용자의 쿠폰을 상세 조회합니다."
    )
    ResponseEntity<ApiResponse<ReadCouponUserDetailRes>> getCouponDetail(@PathVariable UUID couponUserId);
}
