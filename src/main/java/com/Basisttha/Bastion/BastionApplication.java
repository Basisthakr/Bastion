package com.Basisttha.Bastion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BastionApplication {

	public static void main(String[] args) {
		SpringApplication.run(BastionApplication.class, args);
	}

}
