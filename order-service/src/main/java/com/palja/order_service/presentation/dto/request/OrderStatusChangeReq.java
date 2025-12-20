package com.palja.order_service.presentation.dto.request;

import com.palja.order_service.application.command.OrderStatusChangeCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

// 주문 취소 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 상태 변경 요청")
public class OrderStatusChangeReq {

    @NotBlank(message = "변경할 상태는 필수입니다.")
    @Schema(description = "변경할 주문 상태", example = "DELIVERING")
    private String status;

    @NotBlank(message = "변경 사유는 필수입니다.")
    @Size(max = 500, message = "변경 사유는 500자 이하로 입력해주세요.")
    @Schema(description = "변경 사유", example = "상품 출고 완료")
    private String reason;

    public OrderStatusChangeCommand toCommand(UUID orderId, String loginId) {
        return OrderStatusChangeCommand.builder()
                .orderId(orderId)
                .managerLoginId(loginId)
                .status(status)
                .reason(reason)
                .build();
    }
}