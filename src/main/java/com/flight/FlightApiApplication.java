package com.flight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableJpaRepositories
@EnableRetry
public class FlightApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlightApiApplication.class, args);
    }
}
