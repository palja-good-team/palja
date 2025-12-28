package com.palja.timedeal_service.infrastructure.external.feign.client;

import com.palja.common.response.ApiResponse;
import com.palja.timedeal_service.infrastructure.external.feign.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service", path = "/api/v1/products")
public interface ProductFeignClient {

    @GetMapping("/time-deal/{productId}")
    ApiResponse<ProductDTO> getProduct(@PathVariable UUID productId);
}
