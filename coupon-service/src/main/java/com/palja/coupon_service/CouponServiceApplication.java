package com.palja.coupon_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.palja.coupon_service", "com.palja.common"})
public class CouponServiceApplication {

    public static void main(String[] args) {
        System.out.println("asdddd");
        SpringApplication.run(CouponServiceApplication.class, args);
    }

}
