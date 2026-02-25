package com.app.trashmasters.employee.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "drivers") // Tells Mongo to save this in a "drivers" collection
public class Employee {

    @Id // Maps this to the MongoDB "_id" field
    private String id;

    @Indexed(unique = true)
    private String employeeId; // The mandatory business ID (e.g., "DRV-789")

    private String firstName;
    private String lastName;

    private UserRole role = UserRole.DRIVER;

    @Indexed(unique = true) // Ensures you can't have two drivers with the same email
    private String email;

    private String phone;

    // --- Fields that exist in DB but NOT in the Request DTO ---

    // Example: A driver is created as "ACTIVE" by default
    private String status = "ACTIVE";

    // Example: Which truck they are currently driving (can be null)
    private String currentTruckId;
}