package com.app.trashmasters.sensor;


import com.app.trashmasters.bin.BinRepository;
import com.app.trashmasters.bin.model.Bin;
import com.app.trashmasters.bin.model.BinStatus;
import com.app.trashmasters.sensor.dto.SensorDataRequest;
import com.app.trashmasters.sensor.model.SensorReading;
import com.app.trashmasters.sensor.model.SensorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Service
public class SensorIngestionService {

    @Autowired private SensorRepository sensorRepository;
    @Autowired private BinRepository binRepository;
    @Autowired private SensorReadingRepository historyRepository;

    // The Main Pipeline Method
    @Transactional // Ensures all DB saves happen, or none do
    public void processSensorData(SensorDataRequest request) {

        // 1. Validate Sensor & Update Battery
        Sensor sensor = sensorRepository.findById(request.getSensorId())
                .orElseThrow(() -> new RuntimeException("Unknown Sensor ID: " + request.getSensorId()));

        sensor.setBatteryLevel(request.getBattery());
        sensor.setLastUpdated(Instant.now());

        // Check for low battery flag
        if (request.getBattery() < 15) {
            sensor.setStatus(SensorStatus.LOW_BATTERY);
        }
        sensorRepository.save(sensor);

        // 2. Stop if Sensor isn't inside a Bin
        if (sensor.getBinId() == null) {
            System.out.println("Sensor " + request.getSensorId() + " has no Bin assigned. Skipping calculation.");
            return;
        }

        // 3. Fetch Bin & Calculate Fullness
        Bin bin = binRepository.findById(sensor.getBinId())
                .orElseThrow(() -> new RuntimeException("Bin not found"));

        double depth = bin.getDepthCm(); // e.g., 100cm
        double rawDistance = request.getDistanceCm(); // e.g., 20cm

        // Math: (100 - 20) / 100 = 0.80 (80%)
        double fillPercent = ((depth - rawDistance) / depth) * 100.0;

        // Clamp values (Sensor might act weird and read -5cm or 150cm)
        fillPercent = Math.max(0, Math.min(100, fillPercent));

        // 4. Update Bin "Live State" (For Dashboard)
        bin.setFillLevel(fillPercent);
        bin.setLastUpdated(Instant.now());

        // Auto-Status Logic
        if (fillPercent >= 90) bin.setStatus(BinStatus.CRITICAL); // Full!
        else if (fillPercent >= 75) bin.setStatus(BinStatus.FULL); // Getting there
        else bin.setStatus(BinStatus.NORMAL);

        binRepository.save(bin);

        // 5. Create Enriched History (For SageMaker)
        saveToHistory(sensor, bin, request, fillPercent);
    }

    private void saveToHistory(Sensor sensor, Bin bin, SensorDataRequest request, double fillPercent) {
        SensorReading reading = new SensorReading();

        // Basic Info
        reading.setSensorId(sensor.getAssignedId());
        reading.setBinId(bin.getId());
        reading.setRawDistance(request.getDistanceCm());
        reading.setCalculatedFillLevel(fillPercent);
        reading.setBatteryLevel(request.getBattery());
        reading.setTimestamp(Instant.now());

        // --- ENRICHMENT (The "Smart" Part) ---
        LocalDateTime now = LocalDateTime.now(); // Server Time

        reading.setDayOfWeek(now.getDayOfWeek().getValue()); // 1-7
        reading.setHourOfDay(now.getHour()); // 0-23

        boolean isWeekend = (now.getDayOfWeek() == DayOfWeek.SATURDAY || now.getDayOfWeek() == DayOfWeek.SUNDAY);
        reading.setIsWeekend(isWeekend);

        // Mock Weather Logic (Replace with API call later if you have time)
        reading.setTemperatureF(mockTemperature(now.getMonthValue()));
        reading.setIsHoliday(false); // Default to false for now

        historyRepository.save(reading);
    }

    private Double mockTemperature(int month) {
        // Simple heuristic: Summer is hot, Winter is cold
        if (month >= 5 && month <= 9) return 85.0 + (Math.random() * 10);
        return 55.0 + (Math.random() * 10);
    }
}