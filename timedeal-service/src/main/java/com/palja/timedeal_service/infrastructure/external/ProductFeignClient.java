package com.palja.timedeal_service.infrastructure.external;

import com.palja.common.response.ApiResponse;
import com.palja.timedeal_service.infrastructure.external.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "product-service", path = "/api/v1/products")
public interface ProductFeignClient {

    @GetMapping("/time-deal/{productId}")
    ApiResponse<ProductDTO> getProduct(@PathVariable UUID productId);

    @PutMapping("/time-deal/decrease/{productId}")
    void decreaseProductStock(@PathVariable UUID productId, @RequestParam long quantity);

    @PutMapping("/time-deal/increase/{productId}")
    void restoreProductStock(@PathVariable UUID productId, @RequestParam long quantity);
}
