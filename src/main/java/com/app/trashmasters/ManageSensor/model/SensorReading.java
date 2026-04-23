package com.app.trashmasters.ManageSensor.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.Instant;

@Data
@Document(collection = "sensor_readings")
public class SensorReading {

    @Id
    @Schema(example = "69aca61149850123b5131384")
    private String id;

    @Schema(example = "SENSOR-X99")
    private String sensorId;
    @Schema(example = "BEL-BIN-001")
    private String binId;

    @Schema(example = "68.5")
    private Double rawDistance;
    @Schema(example = "14.6")
    private Double calculatedFillLevel;
    @Schema(example = "96")
    private Integer batteryLevel;
    @Schema(example = "2025-12-07T23:26:23.743Z")
    private Instant timestamp;

    @Schema(example = "7", description = "1 (Mon) – 7 (Sun)")
    private Integer dayOfWeek;
    @Schema(example = "15", description = "0–23")
    private Integer hourOfDay;
    @Schema(example = "true")
    private Boolean isWeekend;
    @Schema(example = "51.8")
    private Double temperatureF;
    @Schema(example = "false")
    private Boolean isHoliday;
}