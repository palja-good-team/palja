package com.palja.timedeal_service.presentation.dto.request;

import com.palja.timedeal_service.application.command.DecreaseRemainingQuantityCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class DecreaseRemainingQuantityReq {
    @NotNull
    @Positive(message = "타임딜 재고의 감소 수량은 0보다 커야 합니다.")
    long decreaseQuantity;

    public DecreaseRemainingQuantityCommand toCommand(UUID timeDealId) {
        return DecreaseRemainingQuantityCommand.builder()
                .timeDealId(timeDealId)
                .decreaseQuantity(decreaseQuantity)
                .build();
    }
}
