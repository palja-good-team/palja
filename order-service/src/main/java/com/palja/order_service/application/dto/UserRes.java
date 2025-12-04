package com.palja.order_service.application.dto;

import com.palja.common.vo.UserRole;
import com.palja.order_service.infrastructure.external.dto.response.CustomerUserDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRes {

    private final Long userId;
    private final String loginId;
    private final String name;
    private final String email;
    private final String address;
    private final UserRole role;
    private final String status;
    private final LocalDateTime createdAt;

    // Infrastructure DTO → Application DTO 변환
    public static UserRes from(CustomerUserDTO customerUserDTO) {
        return UserRes.builder()
                .userId(customerUserDTO.getUserId())
                .loginId(customerUserDTO.getLoginId())
                .name(customerUserDTO.getName())
                .email(customerUserDTO.getEmail())
                .address(customerUserDTO.getAddress())
                .role(customerUserDTO.getRole()) // null 허용
                .status(customerUserDTO.getStatus())
                .createdAt(customerUserDTO.getCreatedAt())
                .build();
    }
}