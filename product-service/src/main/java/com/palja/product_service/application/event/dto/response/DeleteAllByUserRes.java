package com.palja.product_service.application.event.dto.response;

import com.palja.product_service.application.event.dto.UserEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteAllByUserRes implements UserEvent {

    private UUID companyUserId;
}
