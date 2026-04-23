package com.app.trashmasters.ManageSensor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SensorFlagRequest {
    @Schema(example = "true")
    private boolean flagged;
    @Schema(example = "No readings for 2 days")
    private String reason;
}