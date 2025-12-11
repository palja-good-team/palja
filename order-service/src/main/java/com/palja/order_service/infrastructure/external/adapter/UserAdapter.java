package com.palja.order_service.infrastructure.external.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.dto.external.CompanyUserRes;
import com.palja.order_service.application.dto.external.CustomerUserRes;
import com.palja.order_service.application.dto.external.ManagerUserRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.port.UserClient;
import com.palja.order_service.infrastructure.external.UserFeignClient;
import com.palja.order_service.infrastructure.external.dto.response.CompanyUserDTO;
import com.palja.order_service.infrastructure.external.dto.response.CustomerUserDTO;
import com.palja.order_service.infrastructure.external.dto.response.ManagerUserDTO;
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
    public CustomerUserRes getMyCustomer(String loginId) {
        log.debug("고객 사용자 조회 요청: loginId={}", loginId);
        try {
            CustomerUserDTO dto = userFeignClient.getMyCustomer().data();
            log.info("고객 사용자 조회 성공: userId={}", dto.getUserId());
            return dto.toResponse();
        } catch (FeignException.NotFound e) {
            log.error("사용자 정보 없음: loginId={}", loginId, e);
            throw new BusinessException(OrderErrorCode.USER_NOT_FOUND);
        } catch (FeignException e) {
            log.error("사용자 서비스 호출 실패: loginId={}, status={}, message={}",
                    loginId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.USER_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("고객 사용자 조회 중 예상치 못한 오류: loginId={}, error={}",
                    loginId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.USER_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public CompanyUserRes getMyCompanyUser(String loginId) {
        log.debug("판매업체 사용자 조회 요청: loginId={}", loginId);
        try {
            CompanyUserDTO dto = userFeignClient.getMyCompanyUser().data();
            log.info("판매업체 사용자 조회 성공: companyUserId={}", dto.getCompanyUserId());
            return dto.toResponse();
        } catch (FeignException.NotFound e) {
            log.error("사용자 정보 없음: loginId={}", loginId, e);
            throw new BusinessException(OrderErrorCode.USER_NOT_FOUND);
        } catch (FeignException e) {
            log.error("사용자 서비스 호출 실패: loginId={}, status={}, message={}",
                    loginId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.USER_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("판매업체 사용자 조회 중 예상치 못한 오류: loginId={}, error={}",
                    loginId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.USER_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public ManagerUserRes getMyManager(String loginId) {
        log.debug("MANAGER 사용자 조회 요청: loginId={}", loginId);
        try {
            ManagerUserDTO dto = userFeignClient.getMyManager().data();
            log.info("MANAGER 사용자 조회 성공: userId={}", dto.getUserId());
            return dto.toResponse();
        } catch (FeignException.NotFound e) {
            log.error("사용자 정보 없음: loginId={}", loginId, e);
            throw new BusinessException(OrderErrorCode.USER_NOT_FOUND);
        } catch (FeignException e) {
            log.error("사용자 서비스 호출 실패: loginId={}, status={}, message={}",
                    loginId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.USER_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("MANAGER 사용자 조회 중 예상치 못한 오류: loginId={}, error={}",
                    loginId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.USER_SERVICE_UNAVAILABLE);
        }
    }
}