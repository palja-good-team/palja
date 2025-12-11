package com.palja.payment_service.infrastructure.external.adapter;

import com.palja.common.auditor.CurrentUser;
import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.payment_service.application.dto.external.UserRes;
import com.palja.payment_service.application.port.UserClient;
import com.palja.payment_service.exception.PaymentErrorCode;
import com.palja.payment_service.infrastructure.external.UserFeignClient;
import com.palja.payment_service.infrastructure.external.dto.response.CompanyUserDTO;
import com.palja.payment_service.infrastructure.external.dto.response.CustomerUserDTO;
import com.palja.payment_service.infrastructure.external.dto.response.ManagerUserDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAdapter implements UserClient {

    private final UserFeignClient userFeignClient;

    @Override
    public UserRes getUserByLoginId(String loginId) {
        log.debug("사용자 조회 요청: loginId={}", loginId);
        
        try {
            UserRole role = CurrentUser.getRole();
            UserRes userRes;
            
            if (UserRole.CUSTOMER.equals(role)) {
                userRes = getCustomerUser(loginId);
            } else if (UserRole.MANAGER.equals(role) || UserRole.MASTER.equals(role)) {
                userRes = getManagerUser(loginId);
            } else if (UserRole.COMPANY_USER.equals(role)) {
                userRes = getCompanyUser(loginId);
            } else {
                log.error("지원하지 않는 사용자 권한: role={}, loginId={}", role, loginId);
                throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
            }
            
            log.info("사용자 조회 성공: loginId={}, userId={}, role={}", 
                    loginId, userRes.getUserId(), userRes.getRole());
            return userRes;
            
        } catch (FeignException.NotFound e) {
            log.error("사용자 정보 없음: loginId={}", loginId, e);
            throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
        } catch (FeignException e) {
            log.error("사용자 서비스 호출 실패: loginId={}, status={}, message={}",
                    loginId, e.status(), e.getMessage(), e);
            throw new BusinessException(PaymentErrorCode.USER_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("사용자 조회 중 예상치 못한 오류: loginId={}, error={}",
                    loginId, e.getClass().getName(), e);
            throw new BusinessException(PaymentErrorCode.USER_SERVICE_UNAVAILABLE);
        }
    }

    private UserRes getCustomerUser(String loginId) {
        log.debug("고객 사용자 조회: loginId={}", loginId);
        CustomerUserDTO dto = userFeignClient.getCustomerUserByLoginId(loginId).data();
        return toUserRes(dto);
    }

    private UserRes getManagerUser(String loginId) {
        log.debug("관리자 사용자 조회: loginId={}", loginId);
        ManagerUserDTO dto = userFeignClient.getManagerUserByLoginId(loginId).data();
        return toUserRes(dto);
    }

    private UserRes getCompanyUser(String loginId) {
        log.debug("업체 사용자 조회: loginId={}", loginId);
        CompanyUserDTO dto = userFeignClient.getCompanyUserByLoginId(loginId).data();
        return toUserRes(dto);
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
