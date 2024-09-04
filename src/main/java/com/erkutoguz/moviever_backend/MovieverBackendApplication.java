package com.erkutoguz.moviever_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class MovieverBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(MovieverBackendApplication.class, args);
	}

}
