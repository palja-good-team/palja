package com.palja.order_service.application.dto;

import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.infrastructure.external.dto.response.UserDTO;
import lombok.Builder;

import java.time.LocalDateTime;

// 사용자 정보 응답 DTO
@Builder
public record UserRes(
        Long userId,
        String loginId,
        String name,
        String email,
        String address,
        UserRole role,
        String status,
        LocalDateTime createdAt
) {
    // Infrastructure DTO → Application DTO 변환
    public static UserRes from(UserDTO userDTO) {
        if (userDTO == null) {
            throw new BusinessException(OrderErrorCode.USER_NOT_FOUND);
        }

        return UserRes.builder()
                .userId(userDTO.getUserId())
                .loginId(userDTO.getLoginId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .address(userDTO.getAddress())
                .role(userDTO.getRole() != null ? userDTO.getRole() : null)
                .status(userDTO.getStatus())
                .createdAt(userDTO.getCreatedAt())
                .build();
    }

    // 주문 가능 여부 검증
    public void validateOrderable() {
        if (!"ACTIVE".equals(status)) {
            throw new BusinessException(OrderErrorCode.INVALID_USER_ID);
        }

        if (UserRole.COMPANY_USER.equals(role)) {
            throw new BusinessException(OrderErrorCode.USER_NOT_ALLOWED);
        }
    }

    // 활성 사용자 여부
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}