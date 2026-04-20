package com.app.trashmasters.routing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapboxMatrixResponse {
    private String code; // Should be "Ok"
    // Mapbox returns a 2D array of seconds as doubles
    private double[][] durations;
}

