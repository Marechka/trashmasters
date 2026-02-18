package com.app.trashmasters.sensor;

import com.app.trashmasters.sensor.model.SensorStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.Instant;

@Data
@Document(collection = "sensors")
public class Sensor {

    @Id
    private String assignedId; // The Hardware Serial Number (e.g., "SENSOR-X99")

    private String binId;      // The Bin this is currently attached to (nullable)
    private Integer batteryLevel; // 0 to 100
    private SensorStatus status;
    private Instant lastUpdated;

    private Boolean isFlagged = false; // Default to false
    private String flagReason;         // Context (optional)

    // Constructor for quick creation
    public Sensor(String assignedId) {
        this.assignedId = assignedId;
        this.status = SensorStatus.INACTIVE;
        this.batteryLevel = 100;
        this.lastUpdated = Instant.now();
        this.isFlagged = false;
    }
}