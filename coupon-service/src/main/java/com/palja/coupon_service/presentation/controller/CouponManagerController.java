package com.palja.coupon_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.dto.CouponDTO;
import com.palja.coupon_service.application.service.CouponManagerService;
import com.palja.coupon_service.presentation.dto.request.CreateCouponReq;
import com.palja.coupon_service.presentation.dto.response.CreateCouponRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manager/coupons")
public class CouponManagerController {

    private final CouponManagerService couponManagerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateCouponRes>> createCoupon(
            @Valid @RequestBody CreateCouponReq request) {
        log.info("POST /api/v1/manager/coupons - 쿠폰 생성 요청");
        CreateCouponCommand command = CreateCouponReq.of(request);

        CouponDTO couponDTO = couponManagerService.createCoupon(command);

        CreateCouponRes couponRes = CreateCouponRes.from(couponDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(couponRes, "쿠폰이 생성되었습니다."));
    }
}
