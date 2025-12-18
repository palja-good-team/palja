package com.palja.timedeal_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = {"com.palja.timedeal_service", "com.palja.common"})
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
public class TimedealServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimedealServiceApplication.class, args);
	}

}
