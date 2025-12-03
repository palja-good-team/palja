package com.palja.coupon_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.dto.CouponRes;
import com.palja.coupon_service.application.service.CouponManagerService;
import com.palja.coupon_service.presentation.dto.request.CreateCouponReq;
import com.palja.coupon_service.presentation.dto.response.CreateCouponRes;
import com.palja.coupon_service.presentation.dto.response.FindCouponDetailRes;
import com.palja.coupon_service.presentation.dto.response.PagedCouponListRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

        CouponRes couponDTO = couponManagerService.createCoupon(command);

        CreateCouponRes couponRes = CreateCouponRes.from(couponDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(couponRes, "쿠폰이 생성되었습니다."));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PagedCouponListRes>> getCouponList(Pageable pageable) {
        log.info("GET /api/v1/manager/coupons - 쿠폰 목록 조회 요청");

        Page<CouponRes> couponResPage = couponManagerService.getCouponList(pageable);

        PageResponse<PagedCouponListRes> response = PageResponse.from(couponResPage.map(PagedCouponListRes::from));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponse<FindCouponDetailRes>> getCouponDetail(@PathVariable UUID couponId) {
        log.info("GET /api/v1/manager/coupons/{} - 쿠폰 상세 조회 요청", couponId);

        FindCouponDetailRes response = FindCouponDetailRes.from(couponManagerService.getCouponDetail(couponId));

        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰 상세 조회"));
    }
}
