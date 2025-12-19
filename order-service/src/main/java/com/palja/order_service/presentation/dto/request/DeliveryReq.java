package com.palja.order_service.presentation.dto.request;

import com.palja.order_service.application.command.DeliveryCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 배송 정보 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "배송 정보 요청")
public class DeliveryReq {

    @NotBlank(message = "수령인 이름은 필수입니다.")
    @Size(max = 50, message = "수령인 이름은 50자를 초과할 수 없습니다.")
    @Schema(description = "수령인 이름", example = "김차돌")
    private String recipientName;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다.")
    @Schema(description = "수령인 이메일", example = "chadoll@example.com")
    private String recipientEmail;

    @NotBlank(message = "수령인 주소는 필수입니다.")
    @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다.")
    @Schema(description = "배송 주소", example = "서울특별시 강남구 테헤란로 123")
    private String recipientAddress;

    @Size(max = 255, message = "배송 메시지는 255자를 초과할 수 없습니다.")
    @Schema(description = "배송 메시지", example = "문 앞에 놓아주세요")
    private String deliveryMessage;

    // DeliveryRequest → DeliveryCommand 변환
    public DeliveryCommand toCommand() {
        return DeliveryCommand.builder()
                .recipientName(recipientName)
                .recipientEmail(recipientEmail)
                .recipientAddress(recipientAddress)
                .deliveryMessage(deliveryMessage)
                .build();
    }
}