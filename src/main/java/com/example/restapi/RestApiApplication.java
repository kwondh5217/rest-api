package com.example.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.ModelAttribute;

@SpringBootApplication
public class RestApiApplication {

    @ModelAttribute
    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }

}
