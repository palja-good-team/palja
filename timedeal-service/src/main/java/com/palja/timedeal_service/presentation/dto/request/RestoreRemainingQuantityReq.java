package com.palja.timedeal_service.presentation.dto.request;

import com.palja.timedeal_service.application.command.RestoreRemainingQuantityCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class RestoreRemainingQuantityReq {

    @NotNull
    @Positive(message = "타임딜 재고의 복구 수량은 0보다 커야 합니다.")
    long restoreQuantity;

    public RestoreRemainingQuantityCommand toCommand(UUID timeDealId) {
        return RestoreRemainingQuantityCommand.builder()
                .timeDealId(timeDealId)
                .restoreQuantity(restoreQuantity)
                .build();
    }
}
