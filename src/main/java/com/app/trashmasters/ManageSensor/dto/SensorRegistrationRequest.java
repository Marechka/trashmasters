package com.app.trashmasters.ManageSensor.dto;


import com.app.trashmasters.ManageSensor.model.SensorStatus;
import lombok.Data;

@Data
public class SensorRegistrationRequest {
    private String sensorId;    // Required
    private String binId;         // Optional
    private Integer batteryLevel; // Optional
    private SensorStatus status;  // Optional
}