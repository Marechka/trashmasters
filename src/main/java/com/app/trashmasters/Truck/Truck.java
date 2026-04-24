package com.app.trashmasters.Truck;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trucks")
public class Truck {
    @Id
    private String id;

    private String truckId;           // e.g., "TRUCK-001"
    private String assignedDriverId;  // e.g., "EMP-123"
    private String driverName;

    // Current load in compacted cubic yards
    private Double currentCompactedYards;

    // Truck capacity
    private Integer maxCapacityYards;

    // Status
    private String status; // "ACTIVE", "MAINTENANCE", "OFFLINE"

    public String getTruckId() {
        return truckId;
    }

    public String getAssignedDriverId() {
        return assignedDriverId;
    }

    public Double getCurrentCompactedYards() {
        return currentCompactedYards != null ? currentCompactedYards : 0.0;
    }
}