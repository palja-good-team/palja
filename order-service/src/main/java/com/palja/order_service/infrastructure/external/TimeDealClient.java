package com.palja.order_service.infrastructure.external;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.infrastructure.external.dto.response.TimeDealDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "timedeal-service", path = "/api/v1/time-deals")
public interface TimeDealClient {

    @GetMapping("/{timeDealId}")
    ApiResponse<TimeDealDTO> getTimeDeal(@PathVariable("timeDealId") UUID timeDealId);
}
