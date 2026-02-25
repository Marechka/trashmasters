package com.app.trashmasters.sensor;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface SensorRepository extends MongoRepository<Sensor, String> {
    // Find sensors that need battery replacement
    List<Sensor> findByBatteryLevelLessThan(int level);

    // Find all sensors attached to a specific bin (if needed)
    List<Sensor> findByBinId(String binId);

    Optional<Sensor> findBySensorId(String sensorId);

    Optional<Sensor> deleteBySensorId(String sensorId);
}