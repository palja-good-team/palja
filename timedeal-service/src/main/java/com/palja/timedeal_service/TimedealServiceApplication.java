package com.palja.timedeal_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {"com.palja.timedeal_service", "com.palja.common"})
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
public class TimedealServiceApplication {

	public static void main(String[] args) {
        System.out.println("ad");
		SpringApplication.run(TimedealServiceApplication.class, args);
	}

}
