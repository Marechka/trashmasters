package com.app.trashmasters.sensor.dto;

import lombok.Data;

@Data
public class SensorDataRequest {
    private String sensorId;   // e.g., "ESP32-MAC-A1"
    private Double distanceCm; // Raw distance from sensor
    private Integer battery;   // 0-100
}