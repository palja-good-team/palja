package com.palja.order_service.presentation.dto.request;

import com.palja.common.vo.UserRole;
import com.palja.order_service.application.command.CreateOrderCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

// 주문 생성 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 생성 요청")
public class CreateOrderReq {

    @NotNull(message = "상품 ID는 필수입니다.")
    @Schema(description = "상품 ID", example = "00000000-0000-0000-2000-000000000000")
    private UUID productId;

    @NotNull(message = "주문 수량은 필수입니다.")
    @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다.")
    @Schema(description = "주문 수량", example = "10")
    private Long quantity;

    @Schema(description = "타임딜 ID", example = "00000000-0000-0000-3000-000000000000")
    private UUID timeDealId;

    @Schema(description = "사용 쿠폰 ID", example = "00000000-0000-0000-4000-000000000000")
    private UUID couponUserId;

    @NotNull(message = "배송 정보는 필수입니다.")
    @Valid
    @Schema(description = "배송 정보")
    private DeliveryReq delivery;

    // Request → Command 변환
    public CreateOrderCommand toCommand(String loginId, UserRole userRole) {
        return CreateOrderCommand.builder()
                .loginId(loginId)
                .userRole(userRole)
                .productId(productId)
                .quantity(quantity)
                .timeDealId(timeDealId)
                .couponUserId(couponUserId)
                .delivery(delivery.toCommand())
                .build();
    }
}