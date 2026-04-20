package com.app.trashmasters.Truck;

import lombok.Data;

@Data
public class TruckRequest {
    private String truckId;              // e.g., "TRK-001"
    private String assignedDriverId;     // Links to Employee (optional)
    private Double currentCompactedYards; // Optional — defaults to 0.0
}

