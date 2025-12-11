package com.palja.payment_service.infrastructure.external.adapter;

import com.palja.common.exception.BusinessException;
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

        UserRes userRes = tryGetUserByLoginId(loginId);

        log.info("사용자 조회 성공: loginId={}, userId={}, role={}",
                loginId, userRes.getUserId(), userRes.getRole());
        return userRes;
    }

    @Override
    public UserRes getUserByUserId(Long userId) {
        log.debug("사용자 조회 요청: userId={}", userId);

        UserRes userRes = tryGetUserByUserId(userId);

        log.info("사용자 조회 성공: userId={}, loginId={}, role={}",
                userId, userRes.getLoginId(), userRes.getRole());
        return userRes;
    }

    private UserRes tryGetUserByLoginId(String loginId) {
        try {
            CustomerUserDTO dto = userFeignClient.getCustomerUserByLoginId(loginId).data();
            if (dto != null) {
                return toUserRes(dto);
            }
        } catch (FeignException.NotFound e) {
            log.debug("Customer 사용자 없음: loginId={}", loginId);
        } catch (FeignException.Forbidden e) {
            log.debug("Customer 사용자 접근 거부 (403): loginId={}", loginId);
        } catch (Exception e) {
            log.debug("Customer 사용자 조회 실패: loginId={}, error={}", loginId, e.getMessage());
        }

        try {
            ManagerUserDTO dto = userFeignClient.getManagerUserByLoginId(loginId).data();
            if (dto != null) {
                return toUserRes(dto);
            }
        } catch (FeignException.NotFound e) {
            log.debug("Manager 사용자 없음: loginId={}", loginId);
        } catch (FeignException.Forbidden e) {
            log.debug("Manager 사용자 접근 거부 (403): loginId={}", loginId);
        } catch (Exception e) {
            log.debug("Manager 사용자 조회 실패: loginId={}, error={}", loginId, e.getMessage());
        }

        try {
            CompanyUserDTO dto = userFeignClient.getCompanyUserByLoginId(loginId).data();
            if (dto != null) {
                return toUserRes(dto);
            }
        } catch (FeignException.NotFound e) {
            log.debug("CompanyUser 사용자 없음: loginId={}", loginId);
        } catch (FeignException.Forbidden e) {
            log.debug("CompanyUser 사용자 접근 거부 (403): loginId={}", loginId);
        } catch (Exception e) {
            log.debug("CompanyUser 사용자 조회 실패: loginId={}, error={}", loginId, e.getMessage());
        }

        log.error("사용자 정보를 찾을 수 없습니다: loginId={}", loginId);
        throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
    }

    private UserRes tryGetUserByUserId(Long userId) {
        try {
            CustomerUserDTO dto = userFeignClient.getCustomerUserByUserId(userId).data();
            if (dto != null) {
                return toUserRes(dto);
            }
        } catch (FeignException.NotFound e) {
            log.debug("Customer 사용자 없음: userId={}", userId);
        } catch (FeignException.Forbidden e) {
            log.debug("Customer 사용자 접근 거부 (403): userId={}", userId);
        } catch (Exception e) {
            log.debug("Customer 사용자 조회 실패: userId={}, error={}", userId, e.getMessage());
        }

        try {
            ManagerUserDTO dto = userFeignClient.getManagerUserByUserId(userId).data();
            if (dto != null) {
                return toUserRes(dto);
            }
        } catch (FeignException.NotFound e) {
            log.debug("Manager 사용자 없음: userId={}", userId);
        } catch (FeignException.Forbidden e) {
            log.debug("Manager 사용자 접근 거부 (403): userId={}", userId);
        } catch (Exception e) {
            log.debug("Manager 사용자 조회 실패: userId={}, error={}", userId, e.getMessage());
        }

        log.error("사용자 정보를 찾을 수 없습니다: userId={}", userId);
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
