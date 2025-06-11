package com.example.tint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TintApplication {

	public static void main(String[] args) {
		SpringApplication.run(TintApplication.class, args);
	}

}
