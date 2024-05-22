package com.example.milky_way_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MilkyWayBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MilkyWayBackApplication.class, args);
    }

}
