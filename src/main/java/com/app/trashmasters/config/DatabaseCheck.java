package com.app.trashmasters.config;


import com.app.trashmasters.driver.DriverRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseCheck {

    @Bean
    CommandLineRunner checkConnection(DriverRepository driverRepository) {
        return args -> {
            System.out.println("-------------------------------------");
            System.out.println("MONGO DB CONNECTION TEST:");
            long count = driverRepository.count();
            System.out.println("Connected! Found " + count + " drivers in the database.");
            System.out.println("-------------------------------------");
        };
    }
}