package com.app.trashmasters.bin.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Double lat;   // Latitude
    private Double lon;   // Longitude

    // Lombok @Data will automatically generate:
    // - getLat(), setLat()
    // - getLon(), setLon()
    // - toString(), equals(), hashCode()
}