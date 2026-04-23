package com.app.trashmasters.ManageSensor;

import com.app.trashmasters.ManageSensor.model.SensorStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.Instant;

@Data
@Document(collection = "sensors")
public class Sensor {

    @Id
    @Schema(example = "69aca61149850123b5131380")
    private String id;

    @Indexed(unique = true)
    @Schema(example = "SENSOR-X99")
    private String sensorId;

    @Schema(example = "BEL-BIN-001")
    private String binId;
    @Schema(example = "96")
    private Integer batteryLevel;
    @Schema(example = "ACTIVE")
    private SensorStatus status;
    @Schema(example = "2026-03-07T22:26:23.548Z")
    private Instant lastUpdated;

    @Schema(example = "true")
    private Boolean isFlagged = false;
    @Schema(example = "No readings for 2 days")
    private String flagReason;

    public Sensor(String sensorId) {
        this.sensorId = sensorId;
        this.status = SensorStatus.INACTIVE;
        this.batteryLevel = 100;
        this.lastUpdated = Instant.now();
        this.isFlagged = false;
    }
}