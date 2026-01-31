package com.app.trashmasters.bin.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bins")
public class Bin {
    @Id
    private String id;

    private String locationName; // e.g., "Bellevue Park - North"

    // Geo-coordinates (Crucial for your Route Generator)
    private double latitude;
    private double longitude;

    private int fillLevel;       // 0 to 100
    private String sensorId;     // e.g., "IoT-X99"

    // Maintenance Status
    private boolean isFlagged;   // True if "Lid Broken" etc.
    private String issueDescription; // "Lid Broken", "Sensor Offline"
}