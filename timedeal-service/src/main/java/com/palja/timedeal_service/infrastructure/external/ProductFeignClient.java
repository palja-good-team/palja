package com.palja.timedeal_service.infrastructure.external;

import com.palja.timedeal_service.infrastructure.external.dto.ProductDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

//@FeignClient(name = "product-service", path = "/api/v1/products")
public interface ProductFeignClient {

    @GetMapping("/time-deal/{productId}")
    ProductDTO getProduct(@PathVariable UUID productId);

    @PutMapping("/time-deal/increase/{productId}/{stock}")
    void restoreProductStock(@PathVariable UUID productId, @PathVariable long stock);
}
