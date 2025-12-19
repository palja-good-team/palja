package com.palja.order_service.infrastructure.external.client.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.dto.external.TimeDealRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.port.client.TimeDealClient;
import com.palja.order_service.infrastructure.external.client.TimeDealFeignClient;
import com.palja.order_service.infrastructure.external.client.dto.request.TimeDealStockDecreaseDTO;
import com.palja.order_service.infrastructure.external.client.dto.request.TimeDealStockRestoreDTO;
import com.palja.order_service.infrastructure.external.client.dto.response.TimeDealDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealClientAdapter implements TimeDealClient {

    private final TimeDealFeignClient timeDealFeignClient;

    @Override
    public TimeDealRes getTimeDeal(UUID timeDealId) {
        log.debug("타임딜 정보 조회 요청: timeDealId={}", timeDealId);
        try {
            TimeDealDTO dto = timeDealFeignClient.getTimeDeal(timeDealId).data();
            log.info("타임딜 정보 조회 성공: timeDealId={}", timeDealId);
            return dto.toResponse();
        } catch (FeignException.NotFound e) {
            log.error("타임딜 정보 없음: timeDealId={}", timeDealId, e);
            throw new BusinessException(OrderErrorCode.TIME_DEAL_NOT_FOUND);
        } catch (FeignException e) {
            log.error("타임딜 서비스 호출 실패: timeDealId={}, status={}, message={}",
                    timeDealId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.TIME_DEAL_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("타임딜 정보 조회 중 예상치 못한 오류: timeDealId={}, error={}",
                    timeDealId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.TIME_DEAL_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void decreaseTimeDealStock(UUID timeDealId, Long quantity) {
        log.info("타임딜 재고 차감 요청 시작: timeDealId={}, quantity={}", timeDealId, quantity);
        try {
            timeDealFeignClient.decreaseTimeDealStock(timeDealId, new TimeDealStockDecreaseDTO(quantity));
            log.info("타임딜 재고 차감 성공: timeDealId={}, quantity={}", timeDealId, quantity);
        } catch (FeignException e) {
            log.error("타임딜 재고 차감 서비스 호출 실패: timeDealId={}, quantity={}, status={}, message={}",
                    timeDealId, quantity, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.TIME_DEAL_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("타임딜 재고 차감 중 예상치 못한 오류: timeDealId={}, quantity={}, error={}",
                    timeDealId, quantity, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.TIME_DEAL_STOCK_DEDUCTION_FAILED);
        }
    }


    @Override
    public void restoreTimeDealStock(UUID timeDealId, Long quantity) {
        log.info("타임딜 재고 복구 요청 시작: timeDealId={}, quantity={}", timeDealId, quantity);
        try {
            timeDealFeignClient.restoreTimeDealStock(timeDealId, new TimeDealStockRestoreDTO(quantity));
            log.info("타임딜 재고 복구 성공: timeDealId={}, quantity={}", timeDealId, quantity);
        } catch (FeignException e) {
            log.error("타임딜 재고 복구 서비스 호출 실패: timeDealId={}, quantity={}, status={}, message={}",
                    timeDealId, quantity, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.TIME_DEAL_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("타임딜 재고 복구 중 예상치 못한 오류: timeDealId={}, quantity={}, error={}",
                    timeDealId, quantity, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.TIME_DEAL_STOCK_RESTORE_FAILED);
        }
    }
}