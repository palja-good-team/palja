package com.palja.order_service.infrastructure.external.adapter;

import com.palja.order_service.application.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductAdapter implements ProductService {

    // TODO: 상품 서비스 연동 시 productClient 주입 및 구현 추가
    //private final ProductClient productClient;
}