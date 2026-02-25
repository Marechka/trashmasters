package com.app.trashmasters.bin.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BinCreateRequest {

    private String binId;
    private String locationName;
    private double latitude;
    private double longitude;
    private Integer depthCm;
    private Double fillLevel;
    private String sensorId;

    // Optional: Allow seeding prediction data during creation
    private Integer predictedFillLevel;
    private LocalDateTime predictionTargetTime;
}