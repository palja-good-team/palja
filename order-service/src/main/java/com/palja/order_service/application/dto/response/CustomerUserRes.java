package com.palja.order_service.application.dto.response;

import com.palja.common.vo.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerUserRes {

    private final Long userId;
    private final String loginId;
    private final String name;
    private final String email;
    private final UserRole role;
    private final String status;
}