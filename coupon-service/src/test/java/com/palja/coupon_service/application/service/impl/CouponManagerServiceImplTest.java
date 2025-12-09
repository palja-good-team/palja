package com.palja.coupon_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.coupon_service.application.command.ChangeCouponStatusCommand;
import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.command.UpdateCouponCommand;
import com.palja.coupon_service.application.dto.coupon.*;
import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.repository.CouponRepository;
import com.palja.coupon_service.domain.vo.*;
import com.palja.coupon_service.exception.CouponErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponManagerService 테스트")
class CouponManagerServiceImplTest {

    @InjectMocks
    private CouponManagerServiceImpl couponManagerService;

    @Mock
    private CouponRepository couponRepository;

    @Nested
    @DisplayName("쿠폰 생성")
    class CreateCouponTest {

        @Test
        @DisplayName("성공")
        void createCoupon_Success() {
            // given
            CreateCouponCommand command = CreateCouponCommand.builder()
                    .couponName("테스트 쿠폰")
                    .description("테스트 쿠폰 설명")
                    .discountType(DiscountType.PERCENTAGE.toString())
                    .discountValue(10)
                    .totalQuantity(1000)
                    .maxDiscountAmount(5000)
                    .minOrderAmount(10000)
                    .issueStartAt(LocalDateTime.of(2025, 12, 1, 0, 0))
                    .issueEndAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                    .usageDays(7)
                    .build();

            Coupon savedCoupon = Coupon.create(
                    command.couponName(),
                    command.description(),
                    DiscountPolicy.of(DiscountType.valueOf(command.discountType()), command.discountValue()),
                    command.totalQuantity(),
                    AmountPolicy.of(command.maxDiscountAmount(), command.minOrderAmount()),
                    IssuePeriod.of(command.issueStartAt(), command.issueEndAt()),
                    command.usageDays()
            );

            given(couponRepository.save(any(Coupon.class))).willReturn(savedCoupon);

            // when
            CreateCouponRes result = couponManagerService.createCoupon(command);

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
        @DisplayName("할인값 0 예외 발생")
        void createCoupon_Invalid_DiscountValue_ThrowException() {
            // given
            CreateCouponCommand command = CreateCouponCommand.builder()
                    .couponName("테스트 쿠폰")
                    .description("테스트 쿠폰 설명")
                    .discountType(DiscountType.PERCENTAGE.toString())
                    .discountValue(0)
                    .totalQuantity(1000)
                    .maxDiscountAmount(5000)
                    .minOrderAmount(10000)
                    .issueStartAt(LocalDateTime.of(2025, 12, 1, 0, 0))
                    .issueEndAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                    .usageDays(7)
                    .build();

            assertThatThrownBy(() -> couponManagerService.createCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("할인값이 유효하지 않습니다.");
        }

        @Test
        @DisplayName("퍼센트 타입 할인값 100 초과 예외 발생")
        void createCoupon_Percentage_Invalid_DiscountValue_ThrowException() {
            // given
            CreateCouponCommand command = CreateCouponCommand.builder()
                    .couponName("테스트 쿠폰")
                    .description("테스트 쿠폰 설명")
                    .discountType(DiscountType.PERCENTAGE.toString())
                    .discountValue(105)
                    .totalQuantity(1000)
                    .maxDiscountAmount(5000)
                    .minOrderAmount(10000)
                    .issueStartAt(LocalDateTime.of(2025, 12, 1, 0, 0))
                    .issueEndAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                    .usageDays(7)
                    .build();

            assertThatThrownBy(() -> couponManagerService.createCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("할인값이 유효하지 않습니다.");
        }

        @Test
        @DisplayName("발급 시작일이 종료 예정일보다 늦을 때 예외 발생")
        void createCoupon_Invalid_IssueStartAt_ThrowException() {
            // given
            CreateCouponCommand command = CreateCouponCommand.builder()
                    .couponName("테스트 쿠폰")
                    .description("테스트 쿠폰 설명")
                    .discountType(DiscountType.PERCENTAGE.toString())
                    .discountValue(10)
                    .totalQuantity(1000)
                    .maxDiscountAmount(5000)
                    .minOrderAmount(10000)
                    .issueStartAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                    .issueEndAt(LocalDateTime.of(2025, 12, 1, 0, 0))
                    .build();

            assertThatThrownBy(() -> couponManagerService.createCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("발급 시작일이 종료일보다 늦을 수 없습니다.");
        }

        @Test
        @DisplayName("중복 쿠폰명 예외 발생")
        void createCoupon_Duplicate_Coupon_Name_ThrowException() {
            // given
            CreateCouponCommand command = CreateCouponCommand.builder()
                    .couponName("테스트 쿠폰")
                    .description("테스트 쿠폰 설명")
                    .discountType(DiscountType.PERCENTAGE.toString())
                    .discountValue(10)
                    .totalQuantity(1000)
                    .maxDiscountAmount(5000)
                    .minOrderAmount(10000)
                    .issueStartAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                    .issueEndAt(LocalDateTime.of(2025, 12, 1, 0, 0))
                    .build();

            given(couponRepository.existsByNameAndDeletedAtIsNull(command.couponName())).willReturn(true);

            assertThatThrownBy(() -> couponManagerService.createCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 존재하는 쿠폰명입니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 수정")
    class UpdateCouponTest {
        @Test
        @DisplayName("수정 성공")
        void updateCoupon_Success() {
            UUID couponId = UUID.randomUUID();
            UpdateCouponCommand command = UpdateCouponCommand.builder()
                    .couponId(couponId)
                    .couponName("수정된 쿠폰명")
                    .description("수정된 쿠폰 설명")
                    .totalQuantity(1000)
                    .maxDiscountAmount(5000)
                    .minOrderAmount(10000)
                    .issueStartAt(LocalDateTime.of(2025, 12, 10, 0, 0))
                    .issueEndAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                    .build();

            Coupon existingCoupon = mock(Coupon.class);
            given(existingCoupon.getDiscountPolicy()).willReturn(mock(DiscountPolicy.class));
            given(existingCoupon.getAmountPolicy()).willReturn(mock(AmountPolicy.class));
            given(existingCoupon.getIssuePeriod()).willReturn(mock(IssuePeriod.class));
            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(existingCoupon));

            // when
            UpdateCouponRes result = couponManagerService.updateCoupon(command);

            // then
            assertThat(result).isNotNull();
            verify(couponRepository).findByIdAndDeletedAtIsNull(couponId);
            verify(existingCoupon).update(
                    command.couponName(),
                    command.description(),
                    command.totalQuantity(),
                    command.maxDiscountAmount(),
                    command.minOrderAmount(),
                    command.issueStartAt(),
                    command.issueEndAt(),
                    command.usageDays()
            );
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰 수정 실패")
        void updateCoupon_NotFound_ThrowException() {
            UUID couponId = UUID.randomUUID();
            UpdateCouponCommand command = UpdateCouponCommand.builder()
                    .couponId(couponId)
                    .couponName("수정된 쿠폰명")
                    .description("수정된 쿠폰 설명")
                    .totalQuantity(1000)
                    .maxDiscountAmount(5000)
                    .minOrderAmount(10000)
                    .issueStartAt(LocalDateTime.of(2025, 12, 10, 0, 0))
                    .issueEndAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                    .build();

            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponManagerService.updateCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("쿠폰을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 상태 변경")
    class ChangeCouponStatusTest {
        @Test
        @DisplayName("쿠폰 만료 상태 변경 성공")
        void changeCouponStatus_ToActive_Success() {
            // given
            UUID couponId = UUID.randomUUID();
            ChangeCouponStatusCommand command = ChangeCouponStatusCommand.builder()
                    .couponId(couponId)
                    .status(CouponStatus.EXPIRED.toString())
                    .build();

            Coupon existingCoupon = mock(Coupon.class);
            given(existingCoupon.getDiscountPolicy()).willReturn(mock(DiscountPolicy.class));
            given(existingCoupon.getAmountPolicy()).willReturn(mock(AmountPolicy.class));
            given(existingCoupon.getIssuePeriod()).willReturn(mock(IssuePeriod.class));
            given(existingCoupon.getStatus()).willReturn(CouponStatus.ACTIVE);
            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(existingCoupon));

            // when
            ChangeStatusCouponRes result = couponManagerService.changeCouponStatus(command);

            // then
            assertThat(result).isNotNull();
            verify(couponRepository).findByIdAndDeletedAtIsNull(couponId);
            verify(existingCoupon).changeStatus(CouponStatus.EXPIRED);
        }

        @Test
        @DisplayName("쿠폰 상태 변경 변경 실패")
        void changeCouponStatus_ToActive_ThrowException() {
            // given
            UUID couponId = UUID.randomUUID();
            ChangeCouponStatusCommand command = ChangeCouponStatusCommand.builder()
                    .couponId(couponId)
                    .status(CouponStatus.ACTIVE.toString())
                    .build();

            Coupon existingCoupon = mock(Coupon.class);
            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(existingCoupon));
            given(existingCoupon.getStatus()).willReturn(CouponStatus.EXPIRED);
            willThrow(new BusinessException(CouponErrorCode.INVALID_STATUS_TRANSITION))
                    .given(existingCoupon).changeStatus(CouponStatus.ACTIVE);

            assertThatThrownBy(() -> couponManagerService.changeCouponStatus(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("유효하지 않은 상태 전환입니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 목록 조회")
    class GetCouponListTest {
        @Test
        @DisplayName("조회 성공")
        void getCouponList_Success() {
            // given
            Coupon coupon1 = mock(Coupon.class);
            given(coupon1.getDiscountPolicy()).willReturn(mock(DiscountPolicy.class));
            given(coupon1.getAmountPolicy()).willReturn(mock(AmountPolicy.class));
            given(coupon1.getIssuePeriod()).willReturn(mock(IssuePeriod.class));

            Coupon coupon2 = mock(Coupon.class);
            given(coupon2.getDiscountPolicy()).willReturn(mock(DiscountPolicy.class));
            given(coupon2.getAmountPolicy()).willReturn(mock(AmountPolicy.class));
            given(coupon2.getIssuePeriod()).willReturn(mock(IssuePeriod.class));

            Pageable pageable = PageRequest.of(0, 10);
            List<Coupon> coupons = List.of(coupon1, coupon2);

            Page<Coupon> couponPage = new PageImpl<>(coupons, pageable, coupons.size());

            given(couponRepository.findAllByDeletedAtIsNull(pageable)).willReturn(couponPage);

            // when
            Page<ReadCouponRes> result = couponManagerService.getCouponList(pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            verify(couponRepository).findAllByDeletedAtIsNull(pageable);
        }

        @Test
        @DisplayName("조회 성공 - 빈 목록")
        void getCouponList_EmptyList_Success() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            Page<Coupon> couponPage = new PageImpl<>(List.of(), pageable, 0);

            given(couponRepository.findAllByDeletedAtIsNull(pageable)).willReturn(couponPage);

            // when
            Page<ReadCouponRes> result = couponManagerService.getCouponList(pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            verify(couponRepository).findAllByDeletedAtIsNull(pageable);
        }
    }

    @Nested
    @DisplayName("쿠폰 상세 조회")
    class getCouponDetailTest {
        @Test
        @DisplayName("상세 조회 성공")
        void getCouponDetail_Success() {
            // given
            UUID couponId = UUID.randomUUID();
            Coupon coupon = mock(Coupon.class);
            given(coupon.getDiscountPolicy()).willReturn(mock(DiscountPolicy.class));
            given(coupon.getAmountPolicy()).willReturn(mock(AmountPolicy.class));
            given(coupon.getIssuePeriod()).willReturn(mock(IssuePeriod.class));

            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));

            // when
            ReadCouponDetailRes result = couponManagerService.getCouponDetail(couponId);

            // then
            assertThat(result).isNotNull();
            verify(couponRepository).findByIdAndDeletedAtIsNull(couponId);
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰 상세 조회 실패")
        void getCouponDetail_NotFound_ThrowException() {
            // given
            UUID couponId = UUID.randomUUID();

            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponManagerService.getCouponDetail(couponId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("쿠폰을 찾을 수 없습니다.");
        }
    }
}

