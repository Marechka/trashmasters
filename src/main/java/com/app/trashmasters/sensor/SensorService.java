package com.app.trashmasters.sensor;


import com.app.trashmasters.sensor.model.SensorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class SensorService {

    @Autowired
    private SensorRepository sensorRepository;

    // 1. Add New Sensor (Onboarding)
    public Sensor registerSensor(String hardwareId) {
        if (sensorRepository.existsById(hardwareId)) {
            throw new RuntimeException("Sensor already exists: " + hardwareId);
        }
        return sensorRepository.save(new Sensor(hardwareId));
    }

    // 2. Delete Sensor (Decommissioning)
    public void deleteSensor(String id) {
        sensorRepository.deleteById(id);
    }

    // 3. Update Battery (Heartbeat from device)
    public Sensor updateBattery(String id, int level) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        sensor.setBatteryLevel(level);
        sensor.setLastUpdated(Instant.now());

        // Auto-flag if battery is critical
        if (level < 20) {
            sensor.setStatus(SensorStatus.LOW_BATTERY);
        } else if (sensor.getStatus() == SensorStatus.LOW_BATTERY) {
            sensor.setStatus(SensorStatus.ACTIVE); // Auto-recover
        }

        return sensorRepository.save(sensor);
    }

    // 4. Flag Sensor (Manual or Automated Issue)
    public Sensor updateStatus(String id, SensorStatus newStatus) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        sensor.setStatus(newStatus);
        sensor.setLastUpdated(Instant.now());
        return sensorRepository.save(sensor);
    }

    // 5. Link to Bin
    public Sensor assignToBin(String sensorId, String binId) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        sensor.setBinId(binId);
        sensor.setStatus(SensorStatus.ACTIVE);
        return sensorRepository.save(sensor);
    }

    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }


    public Sensor setFlag(String id, boolean isFlagged, String reason) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        sensor.setIsFlagged(isFlagged);

        if (isFlagged) {
            // If flagging, save the reason
            sensor.setFlagReason(reason != null ? reason : "Manual Flag");
            // Optionally set status to MAINTENANCE
            sensor.setStatus(SensorStatus.MALFUNCTION);
        } else {
            // If unflagging, clear the reason and reset status
            sensor.setFlagReason(null);
            sensor.setStatus(SensorStatus.ACTIVE);
        }

        sensor.setLastUpdated(Instant.now());
        return sensorRepository.save(sensor);
    }
}