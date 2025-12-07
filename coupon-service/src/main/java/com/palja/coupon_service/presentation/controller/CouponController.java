package com.palja.coupon_service.presentation.controller;

import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.coupon_service.application.command.IssueCouponCommand;
import com.palja.coupon_service.application.dto.CouponUserRes;
import com.palja.coupon_service.application.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/{couponId}")
    public ResponseEntity<ApiResponse<CouponUserRes>> issueCoupon(@PathVariable UUID couponId) {
        log.info("POST /api/v1/coupons/{} - 쿠폰 발급 요청 userId={}", couponId, CurrentUser.getLoginId());

        IssueCouponCommand command = new IssueCouponCommand(couponId, CurrentUser.getLoginId());

        CouponUserRes response = couponService.issueCoupon(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "쿠폰이 발급되었습니다."));
    }
}
