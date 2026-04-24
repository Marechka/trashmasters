package com.app.trashmasters.ManageSensor.dto;

import lombok.Data;

@Data
public class SensorFlagRequest {
    private boolean flagged; // true = flag it, false = unflag it
    private String reason;   // Optional context
}