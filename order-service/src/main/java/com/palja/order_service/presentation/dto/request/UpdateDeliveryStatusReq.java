package com.palja.order_service.presentation.dto.request;

import com.palja.order_service.application.command.UpdateDeliveryStatusCommand;
import com.palja.order_service.domain.vo.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 배송 상태 업데이트 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "배송 상태 업데이트 요청")
public class UpdateDeliveryStatusReq {

    @NotNull(message = "배송 상태는 필수입니다.")
    @Schema(description = "배송 상태", example = "IN_TRANSIT", allowableValues = {"READY", "REQUESTED", "IN_TRANSIT", "DELIVERED"})
    private DeliveryStatus status;

    public UpdateDeliveryStatusCommand toCommand(UUID orderId) {
        return UpdateDeliveryStatusCommand.builder()
                .orderId(orderId)
                .status(status)
                .build();
    }
}