package com.app.trashmasters.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "routes")
public class Route {
    @Id
    private String id;

    private String routeNumber;
    private String driverId;
    private String driverName;

    private List<String> binIds;
    private List<String> completedBinIds;
    private Integer currentStopIndex;

    private String status;  // "CREATED", "IN_PROGRESS", "COMPLETED"
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    private int totalStops;
    private Double totalDistance;
    private Integer estimatedTime;

    // ✅ Track which generation session this route belongs to
    private String generationSession;
}