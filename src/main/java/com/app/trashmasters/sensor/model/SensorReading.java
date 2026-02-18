package com.app.trashmasters.sensor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.Instant;

@Data
@Document(collection = "sensor_readings") // <--- New History Collection
public class SensorReading {

    @Id
    private String id; // Auto-generated UUID

    private String sensorId;
    private String binId;

    // Core Data
    private Double rawDistance;
    private Double calculatedFillLevel;
    private Integer batteryLevel;
    private Instant timestamp;

    // --- ENRICHED DATA FOR XGBOOST ---
    private Integer dayOfWeek;   // 1 (Mon) - 7 (Sun)
    private Integer hourOfDay;   // 0 - 23
    private Boolean isWeekend;
    private Double temperatureF; // Mocked or Real Weather
    private Boolean isHoliday;   // Simple boolean
}