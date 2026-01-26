package com.app.trashmasters.driver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data                  // Lombok: Generates getters, setters, toString, etc.
@NoArgsConstructor     // Lombok: Generates empty constructor (Required by Mongo)
@AllArgsConstructor    // Lombok: Generates constructor with all arguments
@Document(collection = "drivers") // Tells Mongo to save this in a "drivers" collection
public class Driver {

    @Id // Maps this to the MongoDB "_id" field
    private String id;

    private String firstName;
    private String lastName;

    @Indexed(unique = true) // Ensures you can't have two drivers with the same email
    private String email;

    private String phone;

    // --- Fields that exist in DB but NOT in the Request DTO ---

    // Example: A driver is created as "ACTIVE" by default
    private String status = "ACTIVE";

    // Example: Which truck they are currently driving (can be null)
    private String currentTruckId;
}