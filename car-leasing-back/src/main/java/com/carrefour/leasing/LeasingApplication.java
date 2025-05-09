package com.carrefour.leasing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class LeasingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeasingApplication.class, args);
    }

}
