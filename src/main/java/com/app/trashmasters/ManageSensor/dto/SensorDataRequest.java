package com.app.trashmasters.ManageSensor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SensorDataRequest {
    @Schema(example = "SENSOR-X99")
    private String sensorId;
    @Schema(example = "45.5", description = "Raw ultrasonic distance from sensor to trash surface in cm")
    private Double distanceCm;
    @Schema(example = "88")
    private Integer battery;
}