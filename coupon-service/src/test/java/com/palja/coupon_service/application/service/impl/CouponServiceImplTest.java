package com.palja.coupon_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.coupon_service.application.command.ChangeCouponStatusCommand;
import com.palja.coupon_service.application.command.IssueCouponCommand;
import com.palja.coupon_service.application.command.UseCouponCommand;
import com.palja.coupon_service.application.dto.couponUser.*;
import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.entity.CouponUser;
import com.palja.coupon_service.domain.repository.CouponRepository;
import com.palja.coupon_service.domain.repository.CouponUserRepository;
import com.palja.coupon_service.domain.repository.RedisRepository;
import com.palja.coupon_service.domain.vo.AmountPolicy;
import com.palja.coupon_service.domain.vo.CouponUserStatus;
import com.palja.coupon_service.domain.vo.DiscountPolicy;
import com.palja.coupon_service.exception.CouponErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService 테스트")
public class CouponServiceImplTest {

    @InjectMocks
    private CouponServiceImpl couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponUserRepository couponUserRepository;

    @Mock
    private RedisRepository redisRepository;

    @Nested
    @DisplayName("일반 쿠폰 발급")
    class IssueCouponTest {

        @Test
        @DisplayName("성공")
        void issueCoupon_Success() {
            // given
            UUID couponId = UUID.randomUUID();
            String userId = "test";
            IssueCouponCommand command = new IssueCouponCommand(couponId, userId);

            Coupon coupon = mock(Coupon.class);
            given(coupon.getId()).willReturn(couponId);
            given(coupon.getDiscountPolicy()).willReturn(mock(DiscountPolicy.class));
            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));
            given(couponUserRepository.existsByCoupon_IdAndUserIdAndDeletedAtIsNull(couponId, userId)).willReturn(false);

            CouponUser couponUser = mock(CouponUser.class);
            given(couponUser.getCoupon()).willReturn(coupon);
            given(couponUserRepository.save(any(CouponUser.class))).willReturn(couponUser);

            // when
            CreateCouponUserRes result = couponService.issueCoupon(command);

            // then
            assertThat(result).isNotNull();
            verify(couponRepository).findByIdAndDeletedAtIsNull(couponId);
            verify(coupon).validateIssuable();
            verify(coupon).validateIssuePeriod();
            verify(coupon).increaseIssuedQuantity();
            verify(couponUserRepository).save(any(CouponUser.class));
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰 발급 실패 예외 처리")
        void issueCoupon_Not_Fount_ThrowException() {
            // given
            UUID couponId = UUID.randomUUID();
            String userId = "test";
            IssueCouponCommand command = new IssueCouponCommand(couponId, userId);

            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.issueCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("쿠폰을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("발급 불가능 쿠폰 예외 처리")
        void issueCoupon_NotIssuable_ThrowException() {
            UUID couponId = UUID.randomUUID();
            String userId = "test";
            IssueCouponCommand command = new IssueCouponCommand(couponId, userId);

            Coupon coupon = mock(Coupon.class);
            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));
            willThrow(new BusinessException(CouponErrorCode.COUPON_NOT_AVAILABLE)).given(coupon).validateIssuable();

            assertThatThrownBy(() -> couponService.issueCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("발급 가능한 쿠폰이 아닙니다.");
        }

        @Test
        @DisplayName("중복 쿠폰 발급 실패 예외 처리")
        void issueCoupon_Duplicate_ThrowException() {
            UUID couponId = UUID.randomUUID();
            String userId = "test";
            IssueCouponCommand command = new IssueCouponCommand(couponId, userId);

            Coupon coupon = mock(Coupon.class);
            given(coupon.getId()).willReturn(couponId);
            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));
            given(couponUserRepository.existsByCoupon_IdAndUserIdAndDeletedAtIsNull(couponId, userId)).willReturn(true);

            assertThatThrownBy(() -> couponService.issueCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 발급받은 쿠폰입니다.");
        }
    }

    @Nested
    @DisplayName("선착순 쿠폰 발급")
    class IssueFirstComeCouponTest {

        @Test
        @DisplayName("성공")
        void issueFirstComeCoupon_Success() throws InterruptedException {
            // given
            UUID couponId = UUID.randomUUID();
            String userId = "test";
            IssueCouponCommand command = new IssueCouponCommand(couponId, userId);

            Coupon coupon = mock(Coupon.class);
            given(coupon.getId()).willReturn(couponId);
            given(coupon.getDiscountPolicy()).willReturn(mock(DiscountPolicy.class));
            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));

            String lockKey = "lock:coupon:issue:" + couponId;
            RLock lock = mock(RLock.class);

            given(redisRepository.getLockKey(couponId)).willReturn(lockKey);
            given(redisRepository.getLock(lockKey)).willReturn(lock);
            given(lock.tryLock(1, 3, TimeUnit.SECONDS)).willReturn(true);
            given(redisRepository.isDuplicated(couponId, userId)).willReturn(false);
            given(lock.isHeldByCurrentThread()).willReturn(true);

            CouponUser couponUser = mock(CouponUser.class);
            given(couponUser.getCoupon()).willReturn(coupon);
            given(couponUserRepository.save(any(CouponUser.class))).willReturn(couponUser);

            // when
            CreateCouponUserRes result = couponService.issueFirstComeCoupon(command);

            // then
            assertThat(result).isNotNull();
            verify(couponRepository).findByIdAndDeletedAtIsNull(couponId);
            verify(coupon).validateIssuable();
            verify(coupon).validateIssuePeriod();
            verify(redisRepository).initIssuedCount(couponId, 0);
            verify(redisRepository).issued(couponId, userId);
            verify(coupon).increaseIssuedQuantity();
            verify(couponUserRepository).save(any(CouponUser.class));
            verify(lock).unlock();
        }

        @Test
        @DisplayName("락 획득 실패 예외 처리")
        void issueFirstComeCoupon_LockFailed_ThrowException() throws InterruptedException {
            // given
            UUID couponId = UUID.randomUUID();
            String userId = "test";
            IssueCouponCommand command = new IssueCouponCommand(couponId, userId);

            Coupon coupon = mock(Coupon.class);
            given(coupon.getId()).willReturn(couponId);
            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));

            String lockKey = "lock:coupon:issue:" + couponId;
            RLock lock = mock(RLock.class);

            given(redisRepository.getLockKey(couponId)).willReturn(lockKey);
            given(redisRepository.getLock(lockKey)).willReturn(lock);
            given(lock.tryLock(1, 3, TimeUnit.SECONDS)).willReturn(false);

            assertThatThrownBy(() -> couponService.issueFirstComeCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("쿠폰 발급 처리가 중단되었습니다.");
        }

        @Test
        @DisplayName("중복 쿠폰 발급 실패 예외 처리")
        void issueFirstComeCoupon_Duplicate_ThrowException() throws InterruptedException {
            // given
            UUID couponId = UUID.randomUUID();
            String userId = "test";
            IssueCouponCommand command = new IssueCouponCommand(couponId, userId);

            Coupon coupon = mock(Coupon.class);
            given(coupon.getId()).willReturn(couponId);
            given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));

            String lockKey = "lock:coupon:issue:" + couponId;
            RLock lock = mock(RLock.class);

            given(redisRepository.getLockKey(couponId)).willReturn(lockKey);
            given(redisRepository.getLock(lockKey)).willReturn(lock);
            given(lock.tryLock(1, 3, TimeUnit.SECONDS)).willReturn(true);
            given(redisRepository.isDuplicated(couponId, userId)).willReturn(true);

            assertThatThrownBy(() -> couponService.issueFirstComeCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 발급받은 쿠폰입니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 사용")
    class UseCouponTest {

        @Test
        @DisplayName("성공")
        void useCoupon_Success() {
            // given
            UUID couponUserId = UUID.randomUUID();
            String userId = "test";
            UUID orderId = UUID.randomUUID();
            Long discountAmount = 10000L;
            UseCouponCommand command = new UseCouponCommand(
                    couponUserId, userId, orderId, discountAmount);

            Coupon coupon = mock(Coupon.class);
            CouponUser couponUser = mock(CouponUser.class);
            given(couponUser.getCoupon()).willReturn(coupon);
            given(couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(command.couponUserId(), command.userId())).willReturn(Optional.of(couponUser));

            // when
            UsedCouponUserRes result = couponService.useCoupon(command);

            // then
            assertThat(result).isNotNull();
            verify(couponUser).use(command.orderId(), command.discountAmount());
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰 사용 실패 예외 처리")
        void useCoupon_Not_Fount_ThrowException() {
            // given
            UUID couponUserId = UUID.randomUUID();
            String userId = "test";
            UUID orderId = UUID.randomUUID();
            Long discountAmount = 10000L;
            UseCouponCommand command = new UseCouponCommand(
                    couponUserId, userId, orderId, discountAmount);

            given(couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(command.couponUserId(), command.userId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.useCoupon(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("사용자 쿠폰을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 취소")
    class CancelCouponTest {

        @Test
        @DisplayName("성공")
        void cancelCoupon_Success() {
            // given
            UUID couponUserId = UUID.randomUUID();
            String userId = "test";

            CouponUser couponUser = mock(CouponUser.class);
            given(couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(couponUserId, userId)).willReturn(Optional.of(couponUser));

            // when
            CancelCouponUserRes result = couponService.cancelCoupon(couponUserId, userId);

            // then
            assertThat(result).isNotNull();
            verify(couponUser).cancel();
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰 취소 실패 예외 처리")
        void cancelCoupon_Not_Fount_ThrowException() {
            // given
            UUID couponUserId = UUID.randomUUID();
            String userId = "test";

            given(couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(couponUserId, userId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.cancelCoupon(couponUserId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("사용자 쿠폰을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 상태 변경")
    class ChangeCouponStatus {

        @Test
        @DisplayName("성공")
        void changeCouponStatus_Success() {
            // given
            UUID couponUserId = UUID.randomUUID();
            String userId = "test";
            String status = "EXPIRED";
            ChangeCouponStatusCommand command = new ChangeCouponStatusCommand(couponUserId, userId, status);

            CouponUser couponUser = mock(CouponUser.class);
            given(couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(command.couponId(), command.userId())).willReturn(Optional.of(couponUser));

            // when
            ChangeStatusCouponUserRes result = couponService.changeCouponStatus(command);

            // then
            assertThat(result).isNotNull();
            verify(couponUser).changeStatus(CouponUserStatus.valueOf(command.status().toUpperCase()));
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰 상태 변경 실패 예외 처리")
        void changeCouponStatus_Not_Fount_ThrowException() {
            // given
            UUID couponUserId = UUID.randomUUID();
            String userId = "test";
            String status = "EXPIRED";
            ChangeCouponStatusCommand command = new ChangeCouponStatusCommand(couponUserId, userId, status);


            given(couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(command.couponId(), command.userId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.cancelCoupon(couponUserId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("사용자 쿠폰을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 삭제")
    class deleteCoupon {

        @Test
        @DisplayName("성공")
        void deleteCoupon_Success() {
            // given
            UUID couponUserId = UUID.randomUUID();
            String userId = "test";

            CouponUser couponUser = mock(CouponUser.class);
            given(couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(couponUserId, userId)).willReturn(Optional.of(couponUser));

            // when
            DeleteCouponUserRes result = couponService.deleteCoupon(couponUserId, userId);

            // then
            assertThat(result).isNotNull();
            verify(couponUser).softDelete();
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰 삭제 실패 예외 처리")
        void deleteCoupon_Not_Fount_ThrowException() {
            UUID couponUserId = UUID.randomUUID();
            String userId = "test";

            given(couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(couponUserId, userId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.cancelCoupon(couponUserId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("사용자 쿠폰을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 전체 삭제")
    class deleteAllCoupons {

        @Test
        @DisplayName("성공")
        void deleteAllCoupons_Success() {
            // given
            String userId = "test";
            UUID couponUserId1 = UUID.randomUUID();
            UUID couponUserId2 = UUID.randomUUID();

            CouponUser couponUser1 = mock(CouponUser.class);
            CouponUser couponUser2 = mock(CouponUser.class);
            given(couponUser1.getId()).willReturn(couponUserId1);
            given(couponUser2.getId()).willReturn(couponUserId2);

            List<CouponUser> couponUsers = List.of(couponUser1, couponUser2);
            given(couponUserRepository.findAllByUserIdAndDeletedAtIsNull(userId))
                    .willReturn(couponUsers);

            // when
            DeleteAllCouponUserRes result = couponService.deleteAllCoupons(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getDeletedCouponUserIds()).hasSize(2);
            assertThat(result.getDeletedCount()).isEqualTo(2);
            verify(couponUser1).softDelete();
            verify(couponUser2).softDelete();
        }
    }

    @Nested
    @DisplayName("쿠폰 목록 조회")
    class getCouponList {

        @Test
        @DisplayName("성공")
        void getCouponList_Success() {
            // given
            String userId = "test";
            Pageable pageable = PageRequest.of(0, 10);

            CouponUser couponUser1 = mock(CouponUser.class);
            CouponUser couponUser2 = mock(CouponUser.class);
            Coupon coupon1 = mock(Coupon.class);
            Coupon coupon2 = mock(Coupon.class);

            given(couponUser1.getCoupon()).willReturn(coupon1);
            given(couponUser2.getCoupon()).willReturn(coupon2);

            List<CouponUser> couponUsers = List.of(couponUser1, couponUser2);
            Page<CouponUser> couponUserPage = new PageImpl<>(couponUsers, pageable, couponUsers.size());

            given(couponUserRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable)).willReturn(couponUserPage);

            // when
            Page<ReadCouponUserRes> result = couponService.getCouponList(userId, pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("쿠폰 상세 조회")
    class getCouponDetail {

        @Test
        @DisplayName("성공")
        void getCouponDetail_Success() {
            // given
            UUID couponUserId = UUID.randomUUID();
            String userId = "test";

            Coupon coupon = mock(Coupon.class);
            CouponUser couponUser = mock(CouponUser.class);
            given(couponUser.getCoupon()).willReturn(coupon);
            given(coupon.getDiscountPolicy()).willReturn(mock(DiscountPolicy.class));
            given(coupon.getAmountPolicy()).willReturn(mock(AmountPolicy.class));
            given(couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(couponUserId, userId)).willReturn(Optional.of(couponUser));

            // when
            ReadCouponUserDetailRes result = couponService.getCouponDetail(couponUserId, userId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰 조회 실패 예외 처리")
        void getCouponDetail_Not_Fount_ThrowException() {
            UUID couponUserId = UUID.randomUUID();
            String userId = "test";

            given(couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(couponUserId, userId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.getCouponDetail(couponUserId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("사용자 쿠폰을 찾을 수 없습니다.");
        }
    }
}
