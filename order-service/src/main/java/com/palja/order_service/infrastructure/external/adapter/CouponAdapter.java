package com.palja.order_service.infrastructure.external.adapter;

import com.palja.order_service.application.dto.CouponDiscountType;
import com.palja.order_service.application.dto.response.CouponRes;
import com.palja.order_service.application.service.CouponService;
import com.palja.order_service.infrastructure.external.dto.response.CouponCancelDTO;
import com.palja.order_service.infrastructure.external.dto.response.CouponDTO;
import com.palja.order_service.infrastructure.external.dto.response.CouponUseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponAdapter implements CouponService {

    // TODO: 쿠폰 서비스 연동 시 CouponClient 주입 및 구현 추가
    //private final CouponClient couponClient;

    @Override
    public CouponRes getCoupon(UUID couponId) {
        log.debug("쿠폰 정보 조회: couponId={}", couponId);

        // TODO: 실제 쿠폰 서비스 API 호출 (Feign)
        //CouponDTO response = couponClient.getCoupon(couponId).data();
        // 임시 더미 데이터
        CouponDTO response = CouponDTO.dummy(couponId);

        return toCouponRes(response);
    }

    private CouponRes toCouponRes(CouponDTO dto) {

        CouponDiscountType discountType = CouponDiscountType.from(dto.getDiscountType());

        return CouponRes.of(
                dto.getCouponId(),
                dto.getName(),
                discountType,
                dto.getDiscountValue(),
                dto.getMaxDiscountAmount(),
                dto.getMinOrderAmount(),
                dto.getIssueStartAt(),
                dto.getIssueEndAt(),
                dto.getValidityDays(),
                dto.getStatus()
        );
    }

    @Override
    public void useCoupon(UUID couponId, UUID orderId) {
        log.debug("쿠폰 사용 처리: couponId={}, orderId={}", couponId, orderId);

        // TODO: 실제 쿠폰 서비스 API 호출 (Feign)
        // UseCouponDTO request = new UseCouponDTO(orderId);
        // CouponUseDTO response = couponClient.useCoupon(couponId, request).data();
        // TODO: 실제 쿠폰 서비스 연동 시 위의 코드로 교체
        CouponUseDTO response = CouponUseDTO.dummy(couponId, orderId);

        log.info("쿠폰 사용 완료: couponId={}, orderId={}", couponId, orderId);
    }

    @Override
    public void cancelCoupon(UUID couponId, UUID orderId) {
        log.debug("쿠폰 사용 취소: couponId={}", couponId);
        // TODO: 쿠폰 사용 취소 API 호출 구현
        //CancelCouponDTO request = new CancelCouponDTO(orderId);
        //CouponCancelDTO response = couponClient.cancelCoupon(couponId, request).data();
        // TODO: 실제 쿠폰 서비스 연동 시 위의 코드로 교체
        CouponCancelDTO response = CouponCancelDTO.dummy(couponId, orderId);

        log.info("쿠폰 사용 취소 완료: couponId={}", couponId);
    }
}