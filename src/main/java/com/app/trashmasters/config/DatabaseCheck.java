package com.app.trashmasters.config;


import com.app.trashmasters.employee.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;

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

    @Bean
    public SageMakerRuntimeClient sageMakerRuntimeClient() {
        // This builds the client that the SageMakerService is looking for
        return SageMakerRuntimeClient.builder()
                .region(Region.US_EAST_1) // 🛑 Ensure this matches your SageMaker region (e.g., US_EAST_1 or US_WEST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}