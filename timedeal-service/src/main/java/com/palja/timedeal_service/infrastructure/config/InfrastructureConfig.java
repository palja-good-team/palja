package com.palja.timedeal_service.infrastructure.config;

import com.palja.timedeal_service.application.port.ProductClient;
import com.palja.timedeal_service.application.port.UserClient;
import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import com.palja.timedeal_service.infrastructure.external.ProductFeignClient;
import com.palja.timedeal_service.infrastructure.external.UserFeignClient;
import com.palja.timedeal_service.infrastructure.external.adapter.feign.ProductClientAdapter;
import com.palja.timedeal_service.infrastructure.external.adapter.feign.UserClientAdapter;
import com.palja.timedeal_service.infrastructure.repository.JpaTimeDealRepository;
import com.palja.timedeal_service.infrastructure.repository.adapter.TimeDealRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {


    @Bean
    public ProductClient productClient(ProductFeignClient productFeignClient) {
        return new ProductClientAdapter(productFeignClient);
    }

    @Bean
    public UserClient userClient(UserFeignClient userFeignClient) {
        return new UserClientAdapter(userFeignClient);
    }

    @Bean
    public TimeDealRepository timeDealRepository(JpaTimeDealRepository jpaTimeDealRepository) {
        return new TimeDealRepositoryImpl(jpaTimeDealRepository);
    }
}

