package com.palja.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {"com.palja.order_service", "com.palja.common"})
@EnableFeignClients
@EnableDiscoveryClient
@EnableJpaAuditing
public class OrderServiceApplication {

	public static void main(String[] args) {
        System.out.println("ad");
		SpringApplication.run(OrderServiceApplication.class, args);
	}
}