package com.palja.coupon_service.presentation.controller;

import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.coupon_service.application.command.IssueCouponCommand;
import com.palja.coupon_service.application.dto.CouponUserDetailRes;
import com.palja.coupon_service.application.dto.CouponUserRes;
import com.palja.coupon_service.application.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<CouponUserRes>>> getCouponList(Pageable pageable) {
        log.info("GET /api/v1/coupons/me - 사용자 쿠폰 목록 조회 요청 userId={}", CurrentUser.getLoginId());

        Page<CouponUserRes> couponUserResPage = couponService.getCouponList(CurrentUser.getLoginId(), pageable);

        PageResponse<CouponUserRes> response = PageResponse.from(couponUserResPage);

        return ResponseEntity.ok(ApiResponse.success(response, "사용자 쿠폰 목록 조회"));
    }

    @GetMapping("/me/{couponUserId}")
    public ResponseEntity<ApiResponse<CouponUserDetailRes>> getCouponDetail(@PathVariable UUID couponUserId) {
        log.info("GET /api/v1/coupons/me/{} - 쿠폰 상세 조회 요청", couponUserId);

        CouponUserDetailRes response = couponService.getCouponDetail(couponUserId, CurrentUser.getLoginId());

        return ResponseEntity.ok(ApiResponse.success(response, "사용자 쿠폰 상세 조회"));
    }
}
