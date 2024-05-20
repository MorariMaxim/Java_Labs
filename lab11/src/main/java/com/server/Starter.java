package com.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@SpringBootApplication 
public class Starter {
    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
        System.out.println("Server running!");
    }
}
