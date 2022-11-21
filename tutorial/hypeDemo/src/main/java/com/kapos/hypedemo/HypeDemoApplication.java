package com.kapos.hypedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class HypeDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HypeDemoApplication.class, args);
    }


    public void setField(String field) {
    }
}
