package com.app.trashmasters.DataSeederForMongo;

import com.app.trashmasters.ManageSensor.SensorReadingRepository;
import com.app.trashmasters.ManageSensor.model.SensorReading;
import com.app.trashmasters.bin.BinRepository;
import com.app.trashmasters.bin.model.Bin;
import com.app.trashmasters.bin.model.BinStatus;
import com.app.trashmasters.bin.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DataSeederService {

    @Autowired private BinRepository binRepository;
    @Autowired private SensorReadingRepository historyRepository;

    public String seedDatabase() {
        // Safety check to prevent duplicating 20,000 rows accidentally
        if (binRepository.count() > 0) {
            return "Database already has data. Clear MongoDB first if you want to re-seed!";
        }

        Random random = new Random();
        List<Bin> generatedBins = createBellevueBins(30);
        binRepository.saveAll(generatedBins);

        List<SensorReading> allReadings = new ArrayList<>();

        // Go back exactly 90 days from today
        LocalDateTime startTime = LocalDateTime.now().minusDays(90);
        LocalDateTime endTime = LocalDateTime.now();

        for (Bin bin : generatedBins) {
            double currentFill = 0.0;
            LocalDateTime currentTime = startTime;

            while (currentTime.isBefore(endTime)) {
                SensorReading reading = new SensorReading();
                reading.setBinId(bin.getBinId());

                // Convert to Instant for MongoDB
                Instant instant = currentTime.atZone(ZoneId.systemDefault()).toInstant();
                reading.setTimestamp(instant);

                // Simulate realistic fill behavior
                // Add 1% to 6% per hour
                double fillIncrement = 1.0 + (random.nextDouble() * 5.0);

                // If it's the weekend, make it fill up twice as fast! (XGBoost will learn this)
                if (currentTime.getDayOfWeek().getValue() >= 6) {
                    fillIncrement *= 2.0;
                }

                currentFill += fillIncrement;

                // Simulate a garbage truck emptying the bin when it gets too full
                if (currentFill >= 95.0) {
                    currentFill = 0.0;
                }

                reading.setCalculatedFillLevel(Math.min(100.0, currentFill));
                reading.setBatteryLevel(random.nextInt(20) + 80); // Keep battery between 80-100

                // Feature Enrichment
                reading.setDayOfWeek(currentTime.getDayOfWeek().getValue());
                reading.setHourOfDay(currentTime.getHour());
                reading.setIsWeekend(currentTime.getDayOfWeek().getValue() >= 6);

                // Simulate Bellevue weather (cooler in winter, warmer in summer)
                double baseTemp = 50.0;
                reading.setTemperatureF(baseTemp + random.nextDouble() * 15.0);

                allReadings.add(reading);

                // Move forward 1 hour
                currentTime = currentTime.plusHours(1);
            }
        }

        historyRepository.saveAll(allReadings);
        return "Successfully generated " + generatedBins.size() + " Bins and " + allReadings.size() + " Historical Readings!";
    }

    // ... inside DataSeederService.java ...

    private List<Bin> createBellevueBins(int count) {
        List<Bin> bins = new ArrayList<>();
        Random random = new Random();

        // Central Bellevue Coordinate (Downtown Park)
        double baseLat = 47.6101;
        double baseLon = -122.2015;

        for (int i = 0; i < count; i++) {
            Bin bin = new Bin();
            bin.setBinId(String.format("BEL-BIN-%03d", i + 1));
            bin.setDepthCm(120);
            bin.setStatus(BinStatus.NORMAL);
            bin.setFillLevel(0.0);
            bin.setCapacityYards(getRandom());
            bin.setLastUpdated(Instant.now());

            // Generate a random distance up to 20 miles
            // Math.sqrt(random) prevents all bins from clustering in the center
            double radiusMiles = 20.0 * Math.sqrt(random.nextDouble());

            // Generate a random direction (0 to 360 degrees, in radians)
            double angle = random.nextDouble() * 2 * Math.PI;

            // Calculate offsets in miles
            double latOffsetMiles = radiusMiles * Math.sin(angle);
            double lonOffsetMiles = radiusMiles * Math.cos(angle);

            // Convert miles back to degrees (46.5 miles per longitude degree at 47.6N)
            Location loc = new Location();
            loc.setLat(baseLat + (latOffsetMiles / 69.0));
            loc.setLon(baseLon + (lonOffsetMiles / 46.5));
            bin.setLocation(loc);

            bins.add(bin);
        }
        return bins;
    }

    private static int getRandom() {
        int[] binVolume = {2,4,6,8};
        int rnd = new Random().nextInt(binVolume.length);
        return binVolume[rnd];
    }
}