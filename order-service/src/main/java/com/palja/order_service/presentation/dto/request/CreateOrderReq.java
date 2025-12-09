package com.palja.order_service.presentation.dto.request;

import com.palja.order_service.application.command.CreateOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

// 주문 생성 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderReq {

    @NotNull(message = "상품 ID는 필수입니다.")
    private UUID productId;

    @NotNull(message = "주문 수량은 필수입니다.")
    @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다.")
    private int quantity;

    private UUID timeDealId;

    private UUID couponId;

    @NotBlank(message = "결제 Key는 필수입니다.")
    private String paymentKey;

    @NotBlank(message = "결제 수단은 필수입니다.")
    private String paymentMethod;

    @NotNull(message = "배송 정보는 필수입니다.")
    @Valid
    private DeliveryReq delivery;

    // Request → Command 변환
    public CreateOrderCommand toCommand(String loginId) {
        return CreateOrderCommand.builder()
                .loginId(loginId)
                .productId(productId)
                .quantity(quantity)
                .timeDealId(timeDealId)
                .couponId(couponId)
                .paymentKey(paymentKey)
                .paymentMethod(paymentMethod)
                .delivery(delivery.toCommand())
                .build();
    }
}