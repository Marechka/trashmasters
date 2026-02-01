package com.app.trashmasters.bin.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

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

    // The "Real" Reality (Sensor Data)
    private int fillLevel;       // 0 to 100
    private String sensorId;     // e.g., "IoT-X99"

    // The "Predicted" Future (Machine Learning Data)
    // We use Integer (wrapper) so it can be null if no prediction exists
    private Integer predictedFillLevel;

    // When is this prediction for? (e.g., "Predicting fill level for 5:00 PM today")
    private LocalDateTime predictionTargetTime;

    // Maintenance Status
    private boolean isFlagged;   // True if "Lid Broken" etc.
    private String issueDescription; // "Lid Broken", "Sensor Offline"
}