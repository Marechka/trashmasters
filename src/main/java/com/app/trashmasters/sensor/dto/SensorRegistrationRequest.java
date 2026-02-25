package com.app.trashmasters.sensor.dto;


import com.app.trashmasters.sensor.model.SensorStatus;
import lombok.Data;

@Data
public class SensorRegistrationRequest {
    private String sensorId;    // Required
    private String binId;         // Optional
    private Integer batteryLevel; // Optional
    private SensorStatus status;  // Optional
}