package com.app.trashmasters.route;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private String id;
    private String routeNumber;
    private String truckId;
    private String driverId;
    private List<String> binIds;
    private List<RouteStepDTO> steps;
    private Long totalTimeMinutes;
    private Double totalDistance;
    private Long endingTruckVolumeYards;
    private String status;

    // Explicit setters (in case Lombok isn't generating them)
    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public void setTotalTimeMinutes(long totalTimeMinutes) {
        this.totalTimeMinutes = totalTimeMinutes;
    }

    public void setEndingTruckVolumeYards(long volume) {
        this.endingTruckVolumeYards = (long) volume;
    }

    public void setSteps(List<RouteStepDTO> steps) {
        this.steps = steps;
    }
}