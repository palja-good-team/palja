package com.palja.order_service.application.dto;

import com.palja.common.vo.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerUserRes {

    private final Long userId;
    private final String loginId;
    private final String name;
    private final String email;
    private final UserRole role;
    private final String status;

    public static CustomerUserRes of(
            Long userId,
            String loginId,
            String name,
            String email,
            UserRole role,
            String status
    ) {
        return CustomerUserRes.builder()
                .userId(userId)
                .loginId(loginId)
                .name(name)
                .email(email)
                .role(role)
                .status(status)
                .build();
    }
}