package com.palja.timedeal_service.infrastructure.config;

import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import com.palja.timedeal_service.infrastructure.repository.JpaTimeDealRepository;
import com.palja.timedeal_service.infrastructure.repository.adapter.TimeDealRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {

/*    @Bean
    public ProductClient productClient(ProductFeignClient productFeignClient) {
        return new ProductClientImpl(productFeignClient);
    }*/

    @Bean
    public TimeDealRepository timeDealRepository(JpaTimeDealRepository jpaTimeDealRepository) {
        return new TimeDealRepositoryAdapter(jpaTimeDealRepository);
    }
}

