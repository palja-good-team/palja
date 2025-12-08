package com.palja.order_service.infrastructure.external.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealDTO {

    private UUID timeDealId;
    private UUID productId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private BigDecimal timeDealPrice;
    private int discountRate;
    private String status; // ENUM(PENDING, OPEN, SOLD_OUT, CLOSED)
    private int quantity;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;

    // TODO: 타임딜 서비스 연동 전까지 사용하는 더미 데이터. time-deal-service 연결 후 삭제.
    public static TimeDealDTO dummy(UUID timeDealId) {
        return new TimeDealDTO(
                timeDealId,
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                LocalDateTime.of(2025, 12, 1, 0, 0),
                LocalDateTime.of(2025, 12, 10, 23, 59, 59),
                BigDecimal.valueOf(9900),
                10,
                "OPEN",
                37,
                LocalDateTime.of(2025, 12, 3, 19, 0),
                3L,
                LocalDateTime.of(2025, 12, 3, 20, 0),
                3L
        );
    }
}