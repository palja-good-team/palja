package com.palja.order_service.infrastructure.external.adapter;

import com.palja.order_service.application.dto.TimeDealRes;
import com.palja.order_service.application.service.TimeDealService;
import com.palja.order_service.infrastructure.external.dto.response.TimeDealDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealAdapter implements TimeDealService {

    // TODO: 타임딜 서비스 연동 시 timeDealClient 주입 및 구현 추가
    //private final TimeDealClient timeDealClient;

    @Override
    public TimeDealRes getTimeDeal(UUID timeDealId, int quantity) {
        log.debug("타임딜 정보 조회: timeDealId={}", timeDealId);

        //TODO: 실제 타임딜 API 호출 (Feign)
        //TimeDealDTO response = timeDealClient.getTimeDeal(timeDealId).data();
        // TODO: 실제 타임딜 서비스 연동 시 위의 코드로 교체
        // 임시 더미 데이터
        TimeDealDTO response = TimeDealDTO.dummy(timeDealId);

        return TimeDealRes.from(response);
    }

    @Override
    public void deductTimeDealStock(UUID timeDealId, int quantity) {
        log.debug("타임딜 재고 차감 요청: timeDealId={}, quantity={}", timeDealId, quantity);
        // TODO: 타임딜 재고 차감 API 호출 구현
        log.error("타임딜 재고 차감 실패: productId={}, quantity={}", timeDealId, quantity);
    }

    @Override
    public void restoreTimeDealStock(UUID timeDealId, int quantity) {
        log.debug("타임딜 재고 복구 요청: timeDealId={}, quantity={}", timeDealId, quantity);
        // TODO: 상품 재고 차감 API 호출 구현
        log.error("타임딜 재고 복구 실패: timeDealId={}, quantity={}", timeDealId, quantity);
    }
}