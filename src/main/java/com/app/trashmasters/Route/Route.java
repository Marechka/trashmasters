package com.app.trashmasters.route;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "routes")
public class Route {
    @Id
    private String id;

    private String driverId;

    // The ordered list of stops (Bin IDs)
    private List<String> binIds;

    // Status: "CREATED", "IN_PROGRESS", "COMPLETED"
    private String status;

    private LocalDateTime createdAt;

    // Calculated total stops
    private int totalStops;
}