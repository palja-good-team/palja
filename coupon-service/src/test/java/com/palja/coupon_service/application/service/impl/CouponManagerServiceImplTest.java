package com.palja.coupon_service.application.service.impl;

import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.dto.CouponRes;
import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.repository.CouponRepository;
import com.palja.coupon_service.domain.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponManagerService 테스트")
class CouponManagerServiceImplTest {

    @InjectMocks
    private CouponManagerServiceImpl couponManagerService;

    @Mock
    private CouponRepository couponRepository;

    @Test
    @DisplayName("쿠폰 생성 성공")
    void createCoupon_Success() {
        // given
        CreateCouponCommand command = CreateCouponCommand.builder()
                .couponName("테스트 쿠폰")
                .description("테스트 쿠폰 설명")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(10)
                .totalQuantity(1000)
                .maxDiscountAmount(5000)
                .minOrderAmount(10000)
                .issueStartAt(LocalDateTime.of(2025, 12, 1, 0, 0))
                .issueEndAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                .build();

        Coupon savedCoupon = Coupon.create(
                command.couponName(),
                command.description(),
                DiscountPolicy.of(command.discountType(), command.discountValue()),
                command.totalQuantity(),
                AmountPolicy.of(command.maxDiscountAmount(), command.minOrderAmount()),
                IssuePeriod.of(command.issueStartAt(), command.issueEndAt())
        );

        given(couponRepository.save(any(Coupon.class))).willReturn(savedCoupon);

        // when
        CouponRes result = couponManagerService.createCoupon(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCouponName()).isEqualTo("테스트 쿠폰");
        assertThat(result.getDescription()).isEqualTo("테스트 쿠폰 설명");
        assertThat(result.getDiscountType()).isEqualTo(DiscountType.PERCENTAGE);
        assertThat(result.getDiscountValue()).isEqualTo(10);
        assertThat(result.getTotalQuantity()).isEqualTo(1000);
        assertThat(result.getMaxDiscountAmount()).isEqualTo(5000);
        assertThat(result.getMinOrderAmount()).isEqualTo(10000);
        assertThat(result.getIssueStartAt()).isEqualTo(LocalDateTime.of(2025, 12, 1, 0, 0));
        assertThat(result.getIssueEndAt()).isEqualTo(LocalDateTime.of(2025, 12, 31, 23, 59));
        assertThat(result.getStatus()).isEqualTo(CouponStatus.ACTIVE);

        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("쿠폰 생성 할인값 0 예외 발생")
    void createCoupon_Invalid_DiscountValue_ThrowException() {
        // given
        CreateCouponCommand command = CreateCouponCommand.builder()
                .couponName("테스트 쿠폰")
                .description("테스트 쿠폰 설명")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(0)
                .totalQuantity(1000)
                .maxDiscountAmount(5000)
                .minOrderAmount(10000)
                .issueStartAt(LocalDateTime.of(2025, 12, 1, 0, 0))
                .issueEndAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                .build();

        assertThatThrownBy(() -> couponManagerService.createCoupon(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("할인값이 유효하지 않습니다.");
    }

    @Test
    @DisplayName("쿠폰 생성 퍼센트 타입 할인값 100 초과 예외 발생")
    void createCoupon_Percentage_Invalid_DiscountValue_ThrowException() {
        // given
        CreateCouponCommand command = CreateCouponCommand.builder()
                .couponName("테스트 쿠폰")
                .description("테스트 쿠폰 설명")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(105)
                .totalQuantity(1000)
                .maxDiscountAmount(5000)
                .minOrderAmount(10000)
                .issueStartAt(LocalDateTime.of(2025, 12, 1, 0, 0))
                .issueEndAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                .build();

        assertThatThrownBy(() -> couponManagerService.createCoupon(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("할인값이 유효하지 않습니다.");
    }

    @Test
    @DisplayName("쿠폰 생성 발급 시작일이 종료 예정일보다 늦을 때 예외 발생")
    void createCoupon_Invalid_IssueStartAt_ThrowException() {
        // given
        CreateCouponCommand command = CreateCouponCommand.builder()
                .couponName("테스트 쿠폰")
                .description("테스트 쿠폰 설명")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(10)
                .totalQuantity(1000)
                .maxDiscountAmount(5000)
                .minOrderAmount(10000)
                .issueStartAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                .issueEndAt(LocalDateTime.of(2025, 12, 1, 0, 0))
                .build();

        assertThatThrownBy(() -> couponManagerService.createCoupon(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("발급 시작일이 종료일보다 늦을 수 없습니다.");
    }
}