package com.Basisttha.Bastion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
public class BastionApplication {

	public static void main(String[] args) {
		SpringApplication.run(BastionApplication.class, args);
	}

}
