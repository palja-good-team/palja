package com.palja.payment_service.infrastructure.external.dto.response;

import com.palja.common.vo.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerUserDTO {
    private Long userId;
    private String loginId;
    private String name;
    private String email;
    private UserRole role;
    private String status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /*
     TODO: 유저 서비스 연동 전까지 사용하는 더미 데이터
           user-service 연결 후 삭제 예정
     */
    public static ManagerUserDTO dummy(String loginId) {
        return new ManagerUserDTO(
                1L,
                loginId,
                loginId,
                loginId + "@example.com",
                UserRole.MANAGER,
                "ACTIVE",
                LocalDateTime.now(),
                loginId,
                LocalDateTime.now(),
                loginId
        );
    }
}
