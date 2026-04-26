package com.app.trashmasters.ManageSensor.dto;

import com.app.trashmasters.ManageSensor.model.SensorStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SensorRegistrationRequest {
    @Schema(example = "SENSOR-X99")
    private String sensorId;
    @Schema(example = "BIN-101")
    private String binId;
    @Schema(example = "100")
    private Integer batteryLevel;
    @Schema(example = "ACTIVE")
    private SensorStatus status;
}