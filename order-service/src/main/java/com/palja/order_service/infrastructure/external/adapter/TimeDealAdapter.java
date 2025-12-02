package com.palja.order_service.infrastructure.external.adapter;

import com.palja.order_service.application.service.ProductService;
import com.palja.order_service.application.service.TimeDealService;
import com.palja.order_service.infrastructure.external.ProductClient;
import com.palja.order_service.infrastructure.external.TimeDealClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeDealAdapter implements TimeDealService {

    // TODO: 타임딜 서비스 연동 시 timeDealClient 주입 및 구현 추가
    //private final TimeDealClient timeDealClient;
}