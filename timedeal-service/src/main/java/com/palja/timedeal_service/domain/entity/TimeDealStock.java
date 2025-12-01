package com.palja.timedeal_service.domain.entity;

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
public class TimeDealStock {

    @Id
    @Column(name = "time_deal_id")
    private UUID timeDealId;

    @Embedded
    private Quantity quantity;

    @OneToOne
    @MapsId
    @JoinColumn(name = "time_deal_id")
    private TimeDeal timeDeal;
}
