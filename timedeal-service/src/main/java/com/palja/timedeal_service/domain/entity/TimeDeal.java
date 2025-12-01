package com.palja.timedeal_service.domain.entity;

import com.palja.timedeal_service.domain.vo.Amount;
import com.palja.timedeal_service.domain.vo.Period;
import com.palja.timedeal_service.domain.vo.TimeDealStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_time_deal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class TimeDeal {

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
    private TimeDealStatus timeDealStatus = TimeDealStatus.PENDING;

    @OneToOne(mappedBy = "timeDeal",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private TimeDealStock timeDealStock;
}
