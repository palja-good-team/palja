package com.palja.order_service.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class OrderCancellation {

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "canceled_by")
    private String canceledBy;

    public static OrderCancellation create(String cancelReason, String canceledBy) {
        String validatedReason = validateCancelReason(cancelReason);
        String validatedBy = validateCanceledBy(canceledBy);

        return OrderCancellation.builder()
                .canceledAt(LocalDateTime.now())
                .cancelReason(validatedReason)
                .canceledBy(validatedBy)
                .build();
    }

    public boolean isCanceled() {
        return canceledAt != null;
    }

    // ===== 내부 검증 =====
    private static String validateCancelReason(String cancelReason) {
        if (cancelReason == null || cancelReason.isBlank()) {
            throw new IllegalArgumentException("취소 사유는 필수입니다.");
        }
        if (cancelReason.length() > 500) {
            throw new IllegalArgumentException("취소 사유는 500자를 초과할 수 없습니다.");
        }
        return cancelReason.trim();
    }

    private static String validateCanceledBy(String canceledBy) {
        if (canceledBy == null || canceledBy.isBlank()) {
            throw new IllegalArgumentException("취소자 정보는 필수입니다.");
        }
        if (canceledBy.length() > 50) {
            throw new IllegalArgumentException("취소자 정보는 50자를 초과할 수 없습니다.");
        }
        return canceledBy.trim();
    }
}