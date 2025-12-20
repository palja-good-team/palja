package com.palja.order_service.presentation.dto.request;

import com.palja.common.vo.UserRole;
import com.palja.order_service.application.command.CancelOrderCommand;
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
@Schema(description = "주문 취소 요청")
public class CancelOrderReq {

    @NotBlank(message = "취소 사유는 필수입니다.")
    @Size(max = 500, message = "취소 사유는 500자를 초과할 수 없습니다.")
    @Schema(description = "취소 사유", example = "고객 변심으로 인한 취소")
    private String cancelReason;

    public CancelOrderCommand toCommand(UUID orderId, String loginId, UserRole userRole) {
        return CancelOrderCommand.builder()
                .orderId(orderId)
                .CurrentUserLoginId(loginId)
                .CurrentUserRole(userRole)
                .cancelReason(cancelReason)
                .build();
    }
}