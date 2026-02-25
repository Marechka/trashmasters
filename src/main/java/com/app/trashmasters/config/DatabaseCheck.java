package com.app.trashmasters.config;


import com.app.trashmasters.employee.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseCheck {

    @Bean
    CommandLineRunner checkConnection(EmployeeRepository employeeRepository) {
        return args -> {
            System.out.println("-------------------------------------");
            System.out.println("MONGO DB CONNECTION TEST:");
            long count = employeeRepository.count();
            System.out.println("Connected! Found " + count + " drivers in the database.");
            System.out.println("-------------------------------------");
        };
    }
}