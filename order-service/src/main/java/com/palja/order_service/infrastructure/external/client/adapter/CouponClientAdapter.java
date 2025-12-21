package com.palja.order_service.infrastructure.external.client.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.dto.external.CouponUserRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.port.CouponClient;
import com.palja.order_service.infrastructure.external.client.CouponFeignClient;
import com.palja.order_service.infrastructure.external.client.dto.request.UseCouponDTO;
import com.palja.order_service.infrastructure.external.client.dto.response.CancelCouponUserDTO;
import com.palja.order_service.infrastructure.external.client.dto.response.CouponUserDetailDTO;
import com.palja.order_service.infrastructure.external.client.dto.response.UsedCouponUserDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponClientAdapter implements CouponClient {

    private final CouponFeignClient couponFeignClient;

    @Override
    public CouponUserRes getCoupon(UUID couponUserId) {
        log.debug("쿠폰 정보 조회 요청: couponUserId={}", couponUserId);
        try {
            CouponUserDetailDTO dto = couponFeignClient.getMyCouponDetail(couponUserId).data();
            log.info("쿠폰 정보 조회 성공: couponUserId={}", couponUserId);
            return dto.toResponse();
        } catch (FeignException.NotFound e) {
            log.error("쿠폰 정보 없음: couponUserId={}", couponUserId, e);
            throw new BusinessException(OrderErrorCode.COUPON_NOT_FOUND);
        } catch (FeignException e) {
            log.error("쿠폰 서비스 호출 실패: couponUserId={}, status={}, message={}",
                    couponUserId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.COUPON_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("쿠폰 정보 조회 중 예상치 못한 오류: couponUserId={}, error={}",
                    couponUserId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.COUPON_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void useCoupon(UUID couponUserId, UUID orderId, BigDecimal couponDiscountAmount) {
        log.info("쿠폰 사용 요청 시작: couponUserId={}, orderId={}, discountAmount={}",
                couponUserId, orderId, couponDiscountAmount);
        try {
            UseCouponDTO request = new UseCouponDTO(orderId, couponDiscountAmount);
            UsedCouponUserDTO dto = couponFeignClient.useCoupon(couponUserId, request).data();
            log.info("쿠폰 사용 성공: couponUserId={}, orderId={}", couponUserId, orderId);
        } catch (FeignException e) {
            log.error("쿠폰 사용 서비스 호출 실패: couponUserId={}, orderId={}, status={}, message={}",
                    couponUserId, orderId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.COUPON_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("쿠폰 사용 중 예상치 못한 오류: couponUserId={}, orderId={}, error={}",
                    couponUserId, orderId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.COUPON_USE_FAILED);
        }
    }

    @Override
    public void cancelCoupon(UUID couponUserId, UUID orderId) {
        log.info("쿠폰 사용 취소 요청 시작: couponUserId={}, orderId={}", couponUserId, orderId);
        try {
            CancelCouponUserDTO dto = couponFeignClient.cancelCoupon(couponUserId).data();
            log.info("쿠폰 사용 취소 성공: couponUserId={}", couponUserId);
        } catch (FeignException e) {
            log.error("쿠폰 취소 서비스 호출 실패: couponUserId={}, status={}, message={}",
                    couponUserId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.COUPON_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("쿠폰 사용 취소 중 예상치 못한 오류: couponUserId={}, error={}",
                    couponUserId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.COUPON_CANCEL_FAILED);
        }
    }
}