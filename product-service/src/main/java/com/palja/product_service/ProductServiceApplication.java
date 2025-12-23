package com.palja.product_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.palja.product_service", "com.palja.common"})
public class ProductServiceApplication {

	public static void main(String[] args) {
        System.out.println("d");
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
