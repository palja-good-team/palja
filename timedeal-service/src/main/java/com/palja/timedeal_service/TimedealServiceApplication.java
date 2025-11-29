package com.palja.timedeal_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.palja.timedeal_service", "com.palja.common"})
public class TimedealServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimedealServiceApplication.class, args);
	}

}
