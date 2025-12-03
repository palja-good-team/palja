package com.palja.timedeal_service.infrastructure.external;

import com.palja.timedeal_service.application.dto.external.ProductInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

//@FeignClient(name = "product-service", path = "/api/v1/products")
public interface ProductFeignClient {

    @GetMapping("{productId}")
    ProductInfo getProduct(@PathVariable UUID productId);
}
