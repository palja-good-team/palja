package com.palja.product_service.infrastructure.external.kafka.dto;

import com.palja.product_service.application.event.dto.UserEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteAllByUserDto implements UserEvent {

    private UUID companyUserId;
}
