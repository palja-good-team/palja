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

    public static TimeDeal create(
            UUID productId,
            UUID companyUserId,
            String title,
            String description,
            Period period,
            Amount amount,
            Quantity quantity
    ) {
        validate(productId, companyUserId, title, description);

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

    public void changeTitle(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.TITLE_REQUIRED);
        }
        this.title = newTitle;
    }

    public void changeDescription(String newDescription) {
        if (newDescription == null || newDescription.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.DESCRIPTION_REQUIRED);
        }
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

    private static void validate(
            UUID productId,
            UUID companyUserId,
            String title,
            String description
    ) {
        if (productId == null) {
            throw new BusinessException(TimeDealErrorCode.PRODUCT_ID_REQUIRED);
        }

        if (companyUserId == null) {
            throw new BusinessException(TimeDealErrorCode.COMPANY_USER_ID_REQUIRED);
        }

        if (title == null || title.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.TITLE_REQUIRED);
        }

        if (description == null || description.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.DESCRIPTION_REQUIRED);
        }
    }
}
