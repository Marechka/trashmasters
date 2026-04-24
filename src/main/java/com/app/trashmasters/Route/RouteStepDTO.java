package com.app.trashmasters.route;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStepDTO {
    private Double latitude;
    private Double longitude;
    private String type;        // "STATION", "DUMP", "BIN"
    private String binId;       // null for STATION/DUMP
    private String action;      // "START", "PICKUP", "EMPTY_TRUCK", "END"
    private Long etaMinutes;

    // Constructor that matches the calls in SmartRoutingService
    public RouteStepDTO(Double lat, Double lon, String type, String binId, String action, long etaMinutes) {
        this.latitude = lat;
        this.longitude = lon;
        this.type = type;
        this.binId = binId;
        this.action = action;
        this.etaMinutes = etaMinutes;
    }
}