package com.palja.coupon_service.presentation.controller.impl;

import com.palja.common.annotation.RequiredAnonymous;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.coupon_service.application.command.ChangeCouponStatusCommand;
import com.palja.coupon_service.application.command.IssueCouponCommand;
import com.palja.coupon_service.application.command.UseCouponCommand;
import com.palja.coupon_service.application.dto.couponUser.*;
import com.palja.coupon_service.application.service.CouponService;
import com.palja.coupon_service.presentation.controller.CouponController;
import com.palja.coupon_service.presentation.dto.request.ChangeCouponStatusReq;
import com.palja.coupon_service.presentation.dto.request.UseCouponReq;
import jakarta.validation.Valid;
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
public class CouponControllerImpl implements CouponController {

    private final CouponService couponService;

    @Override
    @RequiredAnonymous
    @PostMapping("/{couponId}")
    public ResponseEntity<ApiResponse<CreateCouponUserRes>> issueCoupon(@PathVariable UUID couponId) {
        log.info("POST /api/v1/coupons/{} - 쿠폰 발급 요청 userId={}", couponId, CurrentUser.getLoginId());

        IssueCouponCommand command = new IssueCouponCommand(couponId, CurrentUser.getLoginId());

        CreateCouponUserRes response = couponService.issueCoupon(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "쿠폰이 발급되었습니다."));
    }

    @Override
    @RequiredAnonymous
    @PostMapping("/{couponUserId}/use")
    public ResponseEntity<ApiResponse<UsedCouponUserRes>> useCoupon(@PathVariable UUID couponUserId,
                                                                    @Valid @RequestBody UseCouponReq useCouponReq) {
        log.info("POST /api/v1/coupons/{}/use - 쿠폰 사용 요청 userId={} orderId={}", couponUserId, CurrentUser.getLoginId(), useCouponReq.getOrderId());

        UseCouponCommand command = UseCouponReq.of(couponUserId, CurrentUser.getLoginId(), useCouponReq);

        UsedCouponUserRes response = couponService.useCoupon(command);

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰이 사용되었습니다."));
    }

    @Override
    @RequiredAnonymous
    @PutMapping("/{couponUserId}/cancel")
    public ResponseEntity<ApiResponse<CancelCouponUserRes>> cancelCoupon(@PathVariable UUID couponUserId) {
        log.info("PUT /api/v1/coupons/{}/cancel - 쿠폰 취소 요청 userId={}", couponUserId, CurrentUser.getLoginId());

        CancelCouponUserRes response = couponService.cancelCoupon(couponUserId, CurrentUser.getLoginId());

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰이 취소되었습니다."));
    }

    @Override
    @RequiredAnonymous
    @PutMapping("/{couponUserId}/status")
    public ResponseEntity<ApiResponse<ChangeStatusCouponUserRes>> changeCouponStatus(@PathVariable UUID couponUserId,
                                                                                     @Valid @RequestBody ChangeCouponStatusReq request) {
        log.info("PUT /api/v1/coupons/{}/status - 쿠폰 상태 변경 요청 status: {}", couponUserId, request.getStatus());

        ChangeCouponStatusCommand command = ChangeCouponStatusReq.of(couponUserId, CurrentUser.getLoginId(), request);

        ChangeStatusCouponUserRes response = couponService.changeCouponStatus(command);

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰 상태가 변경되었습니다."));
    }

    @Override
    @RequiredAnonymous
    @DeleteMapping("/{couponUserId}")
    public ResponseEntity<ApiResponse<DeleteCouponUserRes>> deleteCoupon(@PathVariable UUID couponUserId) {
        log.info("DELETE /api/v1/coupons/{} - 쿠폰 삭제 요청 userId={}", couponUserId, CurrentUser.getLoginId());

        DeleteCouponUserRes response = couponService.deleteCoupon(couponUserId, CurrentUser.getLoginId());

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰이 삭제되었습니다."));
    }

    @Override
    @RequiredAnonymous
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<ReadCouponUserRes>>> getCouponList(Pageable pageable) {
        log.info("GET /api/v1/coupons/me - 사용자 쿠폰 목록 조회 요청 userId={}", CurrentUser.getLoginId());

        Page<ReadCouponUserRes> couponUserResPage = couponService.getCouponList(CurrentUser.getLoginId(), pageable);

        PageResponse<ReadCouponUserRes> response = PageResponse.from(couponUserResPage);

        return ResponseEntity.ok(ApiResponse.success(response, "사용자 쿠폰 목록 조회"));
    }

    @Override
    @RequiredAnonymous
    @GetMapping("/me/{couponUserId}")
    public ResponseEntity<ApiResponse<ReadCouponUserDetailRes>> getCouponDetail(@PathVariable UUID couponUserId) {
        log.info("GET /api/v1/coupons/me/{} - 쿠폰 상세 조회 요청", couponUserId);

        ReadCouponUserDetailRes response = couponService.getCouponDetail(couponUserId, CurrentUser.getLoginId());

        return ResponseEntity.ok(ApiResponse.success(response, "사용자 쿠폰 상세 조회"));
    }
}
