package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.entity.OrderDelivery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 송장 정보 응답
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "송장 정보")
public class TrackingRes {

    @Schema(description = "송장 번호", example = "123456789012")
    private String trackingNumber;

    @Schema(description = "택배사", example = "CJ대한통운")
    private String courierCompany;

    @Schema(description = "배송 시작 시간", example = "2025-12-01T12:00:00")
    private LocalDateTime shippedAt;

    @Schema(description = "배송 완료 시간", example = "2025-12-03T15:30:00")
    private LocalDateTime deliveredAt;

    public static TrackingRes from(OrderDelivery delivery) {
        return TrackingRes.builder()
                .trackingNumber(delivery.getTrackingNumber())
                .courierCompany(delivery.getCourierCompany())
                .shippedAt(delivery.getShippedAt())
                .deliveredAt(delivery.getDeliveredAt())
                .build();
    }
}