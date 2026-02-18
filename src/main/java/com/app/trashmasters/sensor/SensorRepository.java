package com.app.trashmasters.sensor;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SensorRepository extends MongoRepository<Sensor, String> {
    // Find sensors that need battery replacement
    List<Sensor> findByBatteryLevelLessThan(int level);

    // Find all sensors attached to a specific bin (if needed)
    List<Sensor> findByBinId(String binId);
}