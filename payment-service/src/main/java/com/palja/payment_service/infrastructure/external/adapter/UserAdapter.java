package com.palja.payment_service.infrastructure.external.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.payment_service.application.dto.response.UserRes;
import com.palja.payment_service.application.service.UserService;
import com.palja.payment_service.exception.PaymentErrorCode;
import com.palja.payment_service.infrastructure.external.UserClient;
import com.palja.payment_service.infrastructure.external.dto.response.CompanyUserDTO;
import com.palja.payment_service.infrastructure.external.dto.response.CustomerUserDTO;
import com.palja.payment_service.infrastructure.external.dto.response.ManagerUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAdapter implements UserService {

    // TODO: 유저 서비스 연동 시 userClient 주입 및 구현 추가
    private final UserClient userClient;

    @Override
    public UserRes getUserByLoginId(String loginId) {
        log.debug("사용자 조회 요청: loginId={}", loginId);
        /*
         TODO: 권한에 따라 다른 엔드포인트 연결하기 (MANAGER, CUSTOMER, COMPANY_USER)
                user-service 연동 시 FeignClient 호출 사용
         */
        // CustomerUserDTO response = userClient.getCustomerUserByLoginId(loginId).data();
        // ManagerUserDTO response = userClient.getManagerUserByLoginId(loginId).data();
        // CompanyUserDTO response = userClient.getCompanyUserByLoginId(loginId).data();

        CustomerUserDTO response = CustomerUserDTO.dummy(loginId);
        return toUserRes(response);
    }

    private UserRes toUserRes(CustomerUserDTO dto) {
        return UserRes.of(
                dto.getUserId(),
                dto.getLoginId(),
                dto.getName(),
                dto.getEmail(),
                dto.getRole(),
                dto.getStatus()
        );
    }

    private UserRes toUserRes(ManagerUserDTO dto) {
        return UserRes.of(
                dto.getUserId(),
                dto.getLoginId(),
                dto.getName(),
                dto.getEmail(),
                dto.getRole(),
                dto.getStatus()
        );
    }

    private UserRes toUserRes(CompanyUserDTO dto) {
        return UserRes.of(
                dto.getUserId(),
                dto.getLoginId(),
                dto.getName(),
                dto.getEmail(),
                dto.getRole(),
                dto.getStatus()
        );
    }
}
