package com.app.trashmasters.DataSeederForMongo;

import com.app.trashmasters.ManageSensor.Sensor;
import com.app.trashmasters.ManageSensor.SensorRepository;
import com.app.trashmasters.ManageSensor.model.SensorStatus;
import com.app.trashmasters.bin.BinRepository;
import com.app.trashmasters.bin.model.Bin;
import com.app.trashmasters.bin.model.BinZone;
import com.app.trashmasters.bin.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * One-time migration service:
 *  - Reassigns coordinates for all bins (10 North Bellevue, 10 South Bellevue, rest Downtown)
 *  - Creates IOT-001 … IOT-NNN sensors and links each to a bin
 */
@Service
public class DataFixService {

    @Autowired private BinRepository binRepository;
    @Autowired private SensorRepository sensorRepository;

    // ── Bellevue coordinate regions ─────────────────────────────────────────
    // North Bellevue  (~47.640–47.655, -122.165 to -122.185)
    private static final double NORTH_LAT  = 47.648;
    private static final double NORTH_LON  = -122.175;

    // South Bellevue  (~47.565–47.580, -122.185 to -122.205)
    private static final double SOUTH_LAT  = 47.572;
    private static final double SOUTH_LON  = -122.195;

    // Downtown Bellevue (~47.610–47.618, -122.198 to -122.210)
    private static final double DOWNTOWN_LAT = 47.614;
    private static final double DOWNTOWN_LON = -122.204;

    // Small jitter radius in degrees (~200 m)
    private static final double JITTER = 0.002;

    public String fixData() {
        List<Bin> bins = binRepository.findAll();
        if (bins.isEmpty()) {
            return "No bins found — seed the database first.";
        }

        Random rng = new Random(42);

        // Delete existing sensors so we don't get duplicate-key errors on re-run
        sensorRepository.deleteAll();

        List<Sensor> sensors = new ArrayList<>();
        int total = bins.size();

        for (int i = 0; i < total; i++) {
            Bin bin = bins.get(i);

            // ── Assign location zone ────────────────────────────────────────
            double baseLat, baseLon;
            BinZone zone;
            if (i < 10) {
                baseLat = NORTH_LAT;
                baseLon = NORTH_LON;
                zone = BinZone.PUBLIC;
                bin.setLocationName("North Bellevue - Zone " + (i + 1));
            } else if (i < 20) {
                baseLat = SOUTH_LAT;
                baseLon = SOUTH_LON;
                zone = BinZone.PUBLIC;
                bin.setLocationName("South Bellevue - Zone " + (i - 9));
            } else {
                baseLat = DOWNTOWN_LAT;
                baseLon = DOWNTOWN_LON;
                zone = BinZone.PUBLIC;
                bin.setLocationName("Downtown Bellevue - Zone " + (i - 19));
            }

            // Add small random jitter so bins aren't stacked exactly on top of each other
            double lat = baseLat + (rng.nextDouble() * 2 - 1) * JITTER;
            double lon = baseLon + (rng.nextDouble() * 2 - 1) * JITTER;
            bin.setLocation(new Location(lat, lon));
            bin.setZone(zone);

            // ── Create matching sensor ───────────────────────────────────────
            String sensorId = String.format("IOT-%03d", i + 1);
            bin.setSensorId(sensorId);

            Sensor sensor = new Sensor(sensorId);
            sensor.setBinId(bin.getBinId());
            sensor.setStatus(SensorStatus.ACTIVE);
            sensor.setBatteryLevel(90 + rng.nextInt(11)); // 90–100
            sensor.setLastUpdated(Instant.now());
            sensors.add(sensor);
        }

        binRepository.saveAll(bins);
        sensorRepository.saveAll(sensors);

        return String.format(
                "Migration complete: updated %d bins (10 North Bellevue, 10 South Bellevue, %d Downtown) " +
                "and created %d sensors (IOT-001 … IOT-%03d).",
                total, total - 20, sensors.size(), sensors.size());
    }
}

