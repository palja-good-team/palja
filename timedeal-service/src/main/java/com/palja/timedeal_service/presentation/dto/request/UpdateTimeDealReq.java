package com.palja.timedeal_service.presentation.dto.request;

import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.UpdateTimeDealCommand;
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

    LocalDateTime startAt;
    LocalDateTime endAt;
    Long timeDealPrice;
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
