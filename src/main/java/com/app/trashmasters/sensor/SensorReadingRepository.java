package com.app.trashmasters.sensor;


import com.app.trashmasters.sensor.model.SensorReading;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SensorReadingRepository extends MongoRepository<SensorReading, String> {
    // Helper to find history for a specific bin
    List<SensorReading> findByBinId(String binId);
}