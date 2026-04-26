package com.app.trashmasters.bin.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Schema(example = "47.6101")
    private Double lat;
    @Schema(example = "-122.2015")
    private Double lon;
}