package com.palja.coupon_service.presentation.controller.impl;

import com.palja.common.annotation.RequiredRole;
import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.coupon_service.application.command.ChangeCouponStatusCommand;
import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.command.UpdateCouponCommand;
import com.palja.coupon_service.application.dto.coupon.*;
import com.palja.coupon_service.application.service.CouponManagerService;
import com.palja.coupon_service.presentation.controller.CouponManagerController;
import com.palja.coupon_service.presentation.dto.request.ChangeCouponStatusReq;
import com.palja.coupon_service.presentation.dto.request.CreateCouponReq;
import com.palja.coupon_service.presentation.dto.request.UpdateCouponReq;
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
@RequestMapping("/api/v1/coupons/manager")
public class CouponManagerControllerImpl implements CouponManagerController {

    private final CouponManagerService couponManagerService;

    @RequiredRole(UserRole.MANAGER)
    @PostMapping
    public ResponseEntity<ApiResponse<CreateCouponRes>> createCoupon(@Valid @RequestBody CreateCouponReq request) {
        log.info("POST /api/v1/coupons/manager - 쿠폰 생성 요청");

        CreateCouponCommand command = CreateCouponReq.of(request);

        CreateCouponRes response = couponManagerService.createCoupon(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "쿠폰이 생성되었습니다."));
    }

    @RequiredRole(UserRole.MANAGER)
    @PutMapping("/{couponId}")
    public ResponseEntity<ApiResponse<UpdateCouponRes>> updateCoupon(@PathVariable UUID couponId, @Valid @RequestBody UpdateCouponReq request) {
        log.info("PUT /api/v1/coupons/manager/{} - 쿠폰 수정 요청", couponId);

        UpdateCouponCommand command = UpdateCouponReq.of(couponId, request);

        UpdateCouponRes response = couponManagerService.updateCoupon(command);

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰이 수정되었습니다."));
    }

    @RequiredRole(UserRole.MANAGER)
    @PutMapping("/{couponId}/status")
    public ResponseEntity<ApiResponse<ChangeStatusCouponRes>> changeCouponStatus(@PathVariable UUID couponId,
                                                                           @Valid @RequestBody ChangeCouponStatusReq request) {
        log.info("PUT /api/v1/coupons/manager/{}/status - 쿠폰 상태 변경 요청 status: {}", couponId, request.getStatus());

        ChangeCouponStatusCommand command = ChangeCouponStatusReq.of(couponId, request);

        ChangeStatusCouponRes response = couponManagerService.changeCouponStatus(command);

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰 상태가 변경되었습니다."));
    }

    @RequiredRole(UserRole.MANAGER)
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReadCouponRes>>> getCouponList(Pageable pageable) {
        log.info("GET /api/v1/coupons/manager - 쿠폰 목록 조회 요청");

        Page<ReadCouponRes> couponResPage = couponManagerService.getCouponList(pageable);

        PageResponse<ReadCouponRes> response = PageResponse.from(couponResPage);

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰 목록 조회"));
    }

    @RequiredRole(UserRole.MANAGER)
    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponse<ReadCouponDetailRes>> getCouponDetail(@PathVariable UUID couponId) {
        log.info("GET /api/v1/coupons/manager/{} - 쿠폰 상세 조회 요청", couponId);

        ReadCouponDetailRes response = couponManagerService.getCouponDetail(couponId);

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰 상세 조회"));
    }
}
