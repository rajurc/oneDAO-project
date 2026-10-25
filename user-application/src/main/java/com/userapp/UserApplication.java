package com.userapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        // This line bootstraps the entire Spring application
        SpringApplication.run(UserApplication.class, args);
    }
}