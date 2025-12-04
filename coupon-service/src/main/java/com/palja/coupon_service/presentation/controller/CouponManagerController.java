package com.palja.coupon_service.presentation.controller;

import com.palja.common.auditor.CurrentUser;
import com.palja.common.exception.BusinessException;
import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.command.UpdateCouponCommand;
import com.palja.coupon_service.application.dto.CouponDetailRes;
import com.palja.coupon_service.application.dto.CouponRes;
import com.palja.coupon_service.application.service.CouponManagerService;
import com.palja.coupon_service.exception.CouponErrorCode;
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
public class CouponManagerController {

    private final CouponManagerService couponManagerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CouponRes>> createCoupon(@Valid @RequestBody CreateCouponReq request) {
        log.info("POST /api/v1/coupons/manager - 쿠폰 생성 요청");

        validateRole();

        CreateCouponCommand command = CreateCouponReq.of(request);

        CouponRes couponRes = couponManagerService.createCoupon(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(couponRes, "쿠폰이 생성되었습니다."));
    }

    @PutMapping("/{couponId}")
    public ResponseEntity<ApiResponse<CouponRes>> updateCoupon(@PathVariable UUID couponId, @Valid @RequestBody UpdateCouponReq request) {
        log.info("PUT /api/v1/coupons/manager/{} - 쿠폰 수정 요청", couponId);

        validateRole();

        UpdateCouponCommand command = UpdateCouponReq.of(couponId, request);

        CouponRes couponRes = couponManagerService.updateCoupon(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(couponRes, "쿠폰이 수정되었습니다."));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CouponRes>>> getCouponList(Pageable pageable) {
        log.info("GET /api/v1/coupons/manager - 쿠폰 목록 조회 요청");

        validateRole();

        Page<CouponRes> couponResPage = couponManagerService.getCouponList(pageable);

        PageResponse<CouponRes> response = PageResponse.from(couponResPage);

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰 목록 조회"));
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponse<CouponDetailRes>> getCouponDetail(@PathVariable UUID couponId) {
        log.info("GET /api/v1/coupons/manager/{} - 쿠폰 상세 조회 요청", couponId);

        validateRole();

        CouponDetailRes response = couponManagerService.getCouponDetail(couponId);

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰 상세 조회"));
    }

    private void validateRole() {
        UserRole role = CurrentUser.getRole();

        if (!UserRole.MASTER.equals(role) && !UserRole.MANAGER.equals(role)) {
            log.warn("권한 없는 사용자의 쿠폰 관리 시도 - id: {}  role: {}", CurrentUser.getLoginId(), role);
            throw new BusinessException(CouponErrorCode.INSUFFICIENT_PERMISSION);
        }
    }
}
