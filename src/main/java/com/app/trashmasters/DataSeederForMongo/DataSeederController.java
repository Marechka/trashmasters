package com.app.trashmasters.DataSeederForMongo;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@Tag(name = "Dev Tools", description = "Development utilities — database seeding")
public class DataSeederController {

    @Autowired
    private DataSeederService dataSeederService;

    @Autowired
    private DataFixService dataFixService;

    // Endpoint: POST /api/dev/seed
    @Operation(summary = "Seed database", description = "Populates MongoDB with sample bins, trucks, employees, and sensors")
    @PostMapping("/seed")
    public ResponseEntity<String> seedData() {
        String result = dataSeederService.seedDatabase();
        return ResponseEntity.ok(result);
    }

    // Endpoint: POST /api/dev/fix-locations-and-sensors
    @Operation(
        summary = "Fix bin locations & create sensors",
        description = "One-time migration: reassigns all bin coordinates to tight Bellevue clusters " +
                      "(10 North, 10 South, rest Downtown) and populates the sensors collection with " +
                      "IOT-001…IOT-NNN linked to each bin."
    )
    @PostMapping("/fix-locations-and-sensors")
    public ResponseEntity<String> fixLocationsAndSensors() {
        String result = dataFixService.fixData();
        return ResponseEntity.ok(result);
    }
}