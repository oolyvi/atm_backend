package com.bank.atm_monolithic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AtmMonolithicApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtmMonolithicApplication.class, args);
	}

}