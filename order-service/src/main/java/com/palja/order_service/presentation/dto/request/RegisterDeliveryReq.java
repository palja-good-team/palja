package com.palja.order_service.presentation.dto.request;

import com.palja.common.vo.UserRole;
import com.palja.order_service.application.command.RegisterDeliveryCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 배송 정보 등록 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "배송 정보 등록 요청")
public class RegisterDeliveryReq {

    @NotBlank(message = "송장 번호는 필수입니다.")
    @Size(max = 100, message = "송장 번호는 100자를 초과할 수 없습니다.")
    @Schema(description = "송장 번호", example = "123456789012")
    private String trackingNumber;

    @NotBlank(message = "택배사는 필수입니다.")
    @Size(max = 50, message = "택배사명은 50자를 초과할 수 없습니다.")
    @Schema(description = "택배사", example = "CJ대한통운")
    private String courierCompany;

    public RegisterDeliveryCommand toCommand(UUID orderId, String loginId, UserRole userRole) {
        return RegisterDeliveryCommand.builder()
                .orderId(orderId)
                .loginId(loginId)
                .userRole(userRole)
                .trackingNumber(trackingNumber)
                .courierCompany(courierCompany)
                .build();
    }
}
