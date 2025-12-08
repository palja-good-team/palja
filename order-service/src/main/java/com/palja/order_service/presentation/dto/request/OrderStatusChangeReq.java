package com.palja.order_service.presentation.dto.request;

import com.palja.order_service.application.command.ChangeOrderStatusCommand;
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
public class OrderStatusChangeReq {

    @NotBlank(message = "변경할 상태는 필수입니다.")
    private String status;

    @NotBlank(message = "변경 사유는 필수입니다.")
    @Size(max = 500, message = "변경 사유는 500자 이하로 입력해주세요.")
    private String reason;

    public ChangeOrderStatusCommand toCommand(UUID orderId, String loginId) {
        return ChangeOrderStatusCommand.builder()
                .orderId(orderId)
                .managerLoginId(loginId)
                .status(status)
                .reason(reason)
                .build();
    }
}