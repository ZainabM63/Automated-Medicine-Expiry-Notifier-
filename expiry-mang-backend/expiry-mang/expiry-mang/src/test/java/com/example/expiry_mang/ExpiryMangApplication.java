
package com.example.expiry_mang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpiryMangApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpiryMangApplication.class, args);
    }
}
