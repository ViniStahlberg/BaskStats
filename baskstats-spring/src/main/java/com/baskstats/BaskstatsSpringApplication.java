package com.baskstats; // ou o pacote onde está a classe principal

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.baskstats")
public class BaskstatsSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaskstatsSpringApplication.class, args);
    }
}