package com.palja.timedeal_service.presentation.dto.request;

import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.UpdateTimeDealCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UpdateTimeDealReq {

    @Size(max = 100, message = "타임딜 제목은 최대 100자까지 입력 가능합니다.")
    String title;
    @Size(max = 500, message = "타임딜 설명은 최대 500자까지 입력 가능합니다.")
    String description;

    @NotNull(message = "타임딜 시작 시간은 필수입니다.")
    LocalDateTime startAt;

    @NotNull(message = "타임딜 종료 시간은 필수입니다.")
    LocalDateTime endAt;

    @NotNull(message = "타임딜 가격은 필수입니다.")
    @Positive(message = "타임딜 가격은 0보다 커야 합니다.")
    Long timeDealPrice;

    @NotNull(message = "타임딜 재고의 전체 수량은 필수입니다.")
    @Positive(message = "타임딜 재고의 전체 수량은 0보다 커야 합니다.")
    Long totalQuantity;

    public UpdateTimeDealCommand toCommand(UUID timeDealId, String loginId, UserRole role) {
        return UpdateTimeDealCommand.builder()
                .timeDealId(timeDealId)
                .title(title)
                .description(description)
                .startAt(startAt)
                .endAt(endAt)
                .timeDealPrice(timeDealPrice)
                .totalQuantity(totalQuantity)
                .loginId(loginId)
                .role(role)
                .build();
    }
}
