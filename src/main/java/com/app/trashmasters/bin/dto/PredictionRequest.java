package com.app.trashmasters.bin.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PredictionRequest {
    private Integer level;
    private LocalDateTime time; // Spring Boot automatically parses "2026-02-01T10:00:00"
}