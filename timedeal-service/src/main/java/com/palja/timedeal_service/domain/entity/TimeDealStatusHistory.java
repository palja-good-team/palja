package com.palja.timedeal_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.timedeal_service.domain.vo.TimeDealStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_time_deal_status_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class TimeDealStatusHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "time_deal_status_history_id")
    private UUID timeDealStatusHistoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private TimeDealStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private TimeDealStatus newStatus;

    @Column(name = "reason", nullable = false, length = 100)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_deal_id", nullable = false)
    private TimeDeal timeDeal;

    public static TimeDealStatusHistory create(
            TimeDeal timeDeal,
            TimeDealStatus previousStatus,
            TimeDealStatus newStatus,
            String reason
    ) {
        return TimeDealStatusHistory.builder()
                .timeDeal(timeDeal)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .reason(reason)
                .build();
    }
}
