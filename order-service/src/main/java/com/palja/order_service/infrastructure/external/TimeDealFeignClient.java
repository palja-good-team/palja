package com.palja.order_service.infrastructure.external;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.infrastructure.external.dto.request.TimeDealStockDecreaseDTO;
import com.palja.order_service.infrastructure.external.dto.request.TimeDealStockRestoreDTO;
import com.palja.order_service.infrastructure.external.dto.response.TimeDealDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "timedeal-service", path = "/api/v1/time-deals")
public interface TimeDealFeignClient {

    // 타임딜 단건 조회
    @GetMapping("/{timeDealId}")
    ApiResponse<TimeDealDTO> getTimeDeal(@PathVariable("timeDealId") UUID timeDealId);

    // 타임딜 재고 차감
    @PutMapping("/{timeDealId}/stock/decrease")
    ApiResponse<Void> decreaseTimeDealStock(
            @PathVariable("timeDealId") UUID timeDealId,
            @RequestBody TimeDealStockDecreaseDTO request
    );

    // 타임딜 재고 복원
    @PutMapping("/{timeDealId}/stock/restore")
    ApiResponse<Void> restoreTimeDealStock(
            @PathVariable("timeDealId") UUID timeDealId,
            @RequestBody TimeDealStockRestoreDTO request
    );
}
