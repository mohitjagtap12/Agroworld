package com.agroworld.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class AgroworldBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgroworldBackendApplication.class, args);
    }
}
