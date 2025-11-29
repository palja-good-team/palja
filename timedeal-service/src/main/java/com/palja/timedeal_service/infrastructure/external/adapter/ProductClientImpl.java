package com.palja.timedeal_service.infrastructure.external.adapter;

import com.palja.timedeal_service.application.port.ProductClient;
import com.palja.timedeal_service.infrastructure.external.ProductFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {

    private final ProductFeignClient productFeignClient;
}
