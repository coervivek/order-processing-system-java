package com.demo.oms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OmsJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OmsJavaApplication.class, args);
    }

}
