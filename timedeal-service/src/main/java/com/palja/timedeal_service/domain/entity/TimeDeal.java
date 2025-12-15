package com.palja.timedeal_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.common.exception.BusinessException;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import com.palja.timedeal_service.domain.vo.Amount;
import com.palja.timedeal_service.domain.vo.Period;
import com.palja.timedeal_service.domain.vo.Quantity;
import com.palja.timedeal_service.domain.vo.TimeDealStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_time_deal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class TimeDeal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "time_deal_id")
    private UUID timeDealId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "company_user_id", nullable = false)
    private UUID companyUserId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Embedded
    private Period period;

    @Embedded
    private Amount amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_deal_status", nullable = false)
    @Builder.Default
    private TimeDealStatus timeDealStatus = TimeDealStatus.PENDING;

    @OneToOne(
            mappedBy = "timeDeal",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private TimeDealStock timeDealStock;

    @OneToMany(
            mappedBy = "timeDeal",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<TimeDealStatusHistory> statusHistories = new ArrayList<>();

    public static TimeDeal create(
            UUID productId,
            UUID companyUserId,
            String title,
            String description,
            Period period,
            Amount amount,
            Quantity quantity
    ) {
        validate(productId, companyUserId, title, description, period, LocalDateTime.now());

        TimeDeal timeDeal = TimeDeal.builder()
                .productId(productId)
                .companyUserId(companyUserId)
                .title(title)
                .description(description)
                .period(period)
                .amount(amount)
                .build();

        timeDeal.timeDealStock = TimeDealStock.create(timeDeal, quantity);

        return timeDeal;
    }

    // ========== 변경 ==========
    public void changeTitle(String newTitle) {
        validateTitle(newTitle);
        this.title = newTitle;
    }

    public void changeDescription(String newDescription) {
        validateDescription(newDescription);
        this.description = newDescription;
    }

    public void changeStartAt(LocalDateTime newStartAt) {
        this.period = this.period.updateStartAt(newStartAt);
    }

    public void changeEndAt(LocalDateTime newEndAt) {
        this.period = this.period.updateEndAt(newEndAt);
    }

    public void changeTimeDealPrice(long newTimeDealPrice) {
        this.amount = this.amount.updateTimeDealPrice(newTimeDealPrice);
    }

    public void changeTotalQuantity(long newTotalQuantity) {
        this.timeDealStock.changeTotalQuantity(newTotalQuantity);
    }

    // ========== 상태 ==========
    public TimeDealStatusHistory changeStatusBy(TimeDealStatus newStatus, String reason) {
        if (newStatus == TimeDealStatus.OPEN) {
            return openNow(reason);
        }

        if (newStatus == TimeDealStatus.CLOSED) {
            return closeNow(reason);
        }

        return changeStatus(newStatus, reason);
    }

    public TimeDealStatusHistory changeStatus(TimeDealStatus newStatus, String reason) {
        validateTimeDealStatusChange(newStatus, reason);

        TimeDealStatusHistory history = TimeDealStatusHistory.create(this, this.timeDealStatus, newStatus, reason);

        this.statusHistories.add(history);
        this.timeDealStatus = newStatus;

        return history;
    }

    public TimeDealStatusHistory openNow(String reason) {
        this.period = this.period.updateStartAt(LocalDateTime.now());

        return changeStatus(TimeDealStatus.OPEN, reason);
    }

    public TimeDealStatusHistory closeNow(String reason) {
        this.period = this.period.updateEndAt(LocalDateTime.now());

        return changeStatus(TimeDealStatus.CLOSED, reason);
    }

    // ========== 재고 ==========
    public void decreaseRemainingQuantity(long decreaseQuantity) {
        ensureDecreasableStock();
        ensureNowWithinPeriod(LocalDateTime.now());

        this.timeDealStock.decreaseRemainingQuantity(decreaseQuantity);

        if (this.timeDealStock.getQuantity().isSoldOut()) {
            this.timeDealStatus = TimeDealStatus.SOLD_OUT;
        }
    }

    public void restoreRemainingQuantity(long restoreQuantity) {
        if (!timeDealStatus.canRestoreStock()) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_CLOSED_RESTORE_EXTERNAL);
        }

        timeDealStock.restoreRemainingQuantity(restoreQuantity);
    }

    // ========== 삭제 ==========
    @Override
    public void softDelete() {
        super.softDelete();
        timeDealStock.softDelete();
    }

    public boolean isDeletable(LocalDateTime now) {
        validateDeletableStatus();
        validateDeletablePeriod(now);
        return true;
    }

    public void validateDeletableStatus() {
        if (!this.timeDealStatus.canDelete()) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_NOT_DELETABLE_STATUS);
        }
    }

    public void validateDeletablePeriod(LocalDateTime now) {
        if (!this.period.isBeforeStartAt(now)) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_NOT_DELETABLE_PERIOD);
        }
    }

    public long getRestoreQuantityOnDelete() {
        return timeDealStock.getQuantity().getRemainingQuantity();
    }

    // ========== 검증 ==========
    private static void validate(
            UUID productId,
            UUID companyUserId,
            String title,
            String description,
            Period period,
            LocalDateTime now
    ) {
        validateProductId(productId);
        validateCompanyUserId(companyUserId);
        validateTitle(title);
        validateDescription(description);
        validateCreatablePeriod(period, now);
    }

    private static void validateProductId(UUID productId) {
        if (productId == null) {
            throw new BusinessException(TimeDealErrorCode.PRODUCT_ID_REQUIRED);
        }
    }

    private static void validateCompanyUserId(UUID companyUserId) {
        if (companyUserId == null) {
            throw new BusinessException(TimeDealErrorCode.COMPANY_USER_ID_REQUIRED);
        }
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.TITLE_REQUIRED);
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.DESCRIPTION_REQUIRED);
        }
    }

    private static void validateCreatablePeriod(Period period, LocalDateTime now) {
        if (!period.getStartAt().isAfter(now)) {
            throw new BusinessException(TimeDealErrorCode.PERIOD_START_TIME_INVALID);
        }
    }

    private void validateTimeDealStatusChange(TimeDealStatus newStatus, String reason) {
        if (newStatus == null) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_STATUS_REQUIRED);
        }

        if (reason == null || reason.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_STATUS_REASON_REQUIRED);
        }

        if (this.timeDealStatus == newStatus) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_STATUS_ALREADY_APPLIED);
        }

        if (!this.timeDealStatus.canTransitTo(newStatus)) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_INVALID_STATUS_TRANSITION);
        }
    }

    private void ensureDecreasableStock(){
        if (!timeDealStatus.isOpen()) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_NOT_OPEN);
        }
    }

    private void ensureNowWithinPeriod(LocalDateTime now) {
        if (!this.period.isNowWithin(now)){
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_NOT_IN_PERIOD);
        }
    }

    public boolean isClosed() {
        return timeDealStatus.isClosed();
    }
}
