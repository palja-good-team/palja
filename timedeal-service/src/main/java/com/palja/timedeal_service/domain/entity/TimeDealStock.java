package com.palja.timedeal_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.common.exception.BusinessException;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import com.palja.timedeal_service.domain.vo.Quantity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_time_deal_stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class TimeDealStock extends BaseEntity {

    @Id
    @Column(name = "time_deal_id")
    private UUID timeDealId;

    @Embedded
    private Quantity quantity;

    @OneToOne
    @MapsId
    @JoinColumn(name = "time_deal_id")
    private TimeDeal timeDeal;

    public static TimeDealStock create(TimeDeal timeDeal, Quantity quantity) {
        TimeDealStock timeDealStock = TimeDealStock.builder()
                .timeDeal(timeDeal)
                .quantity(quantity)
                .build();

        return timeDealStock;
    }

    public void changeTotalQuantity(long newTotalQuantity) {
        this.quantity = this.quantity.updateTotalQuantity(newTotalQuantity);
    }

    public void decreaseRemainingQuantity(long decreaseQuantity) {
        this.quantity = this.quantity.decreaseRemainingQuantity(decreaseQuantity);
    }
}
