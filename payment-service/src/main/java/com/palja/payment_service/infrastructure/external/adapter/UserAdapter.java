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

    private final UserClient userClient;

    @Override
    public UserRes getUserByLoginId(String loginId){
        log.debug("사용자 조회 요청: loginId={}", loginId);

        try {
            CustomerUserDTO customer = userClient.getCustomerUserByLoginId(loginId).data();
            if (customer != null) {
                return toUserRes(customer);
            }
        } catch (Exception e) {
            log.debug("Customer 조회 실패, Manager 조회 시도: {}", e.getMessage());
        }

        try {
            ManagerUserDTO manager = userClient.getManagerUserByLoginId(loginId).data();
            if (manager != null) {
                return toUserRes(manager);
            }
        } catch (Exception e) {
            log.debug("Manager 조회 실패, CompanyUser 조회 시도: {}", e.getMessage());
        }

        try {
            CompanyUserDTO companyUser = userClient.getCompanyUserByLoginId(loginId).data();
            if (companyUser != null) {
                return toUserRes(companyUser);
            }
        } catch (Exception e) {
            log.error("모든 사용자 타입 조회 실패: loginId={}", loginId, e);
            throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
        }

        throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
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
