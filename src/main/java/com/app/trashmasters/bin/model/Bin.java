package com.app.trashmasters.bin.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bins")
public class Bin {
    @Id
    private String id;

    @Indexed(unique = true)
    private String binId; // The physical label/asset tag (e.g., "BIN-101")

    private String locationName; // e.g., "Bellevue Park - North"

    // Geo-coordinates (Crucial for your Route Generator)
    private double latitude;
    private double longitude;

    // The "Real" Reality (Sensor Data)
    private Double fillLevel;
    private Instant lastUpdated;
    private int depthCm; // 0 to 100
    private String sensorId;     // e.g., "IoT-X99"

    private BinStatus status;

    // The "Predicted" Future (Machine Learning Data)
    private Integer predictedFillLevel;
    private LocalDateTime predictionTargetTime;

    private Map<Integer, Double> futurePredictions;

    private BinZone zone;

    private LocalDateTime lastCollected;

    private Integer capacityYards;

    // Maintenance Status
    private boolean isFlagged;   // True if "Lid Broken" etc.
    private String issue; // "Lid Broken", "Sensor Offline"

    public Map<Integer, Double> getFuturePredictions() {
        if (futurePredictions == null) {
            futurePredictions = new HashMap<>();
        }
        return futurePredictions;
    }

    public Integer getCapacityYards() {
        return capacityYards != null ? capacityYards : (depthCm / 100 * 10); // Default calculation
    }

    public Integer getDaysOverdue() {
        if (lastCollected == null) {
            return 0; // Never collected, not overdue
        }
        long days = ChronoUnit.DAYS.between(lastCollected, LocalDateTime.now());
        return days > 0 ? (int) days : 0;
    }

    public Location getLocation() {
        return new Location(latitude, longitude);
    }

    public void setLastCollected(LocalDateTime now) {
        this.lastCollected = now;
    }
}