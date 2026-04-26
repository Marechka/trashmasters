package com.app.trashmasters.ManageSensor;

import com.app.trashmasters.ManageSensor.dto.SensorDataRequest;
import com.app.trashmasters.ManageSensor.dto.SensorFlagRequest;
import com.app.trashmasters.ManageSensor.dto.SensorRegistrationRequest;
import com.app.trashmasters.ManageSensor.model.SensorStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@CrossOrigin(origins = "*")
@Tag(name = "Sensors", description = "IoT sensor registration, data ingestion, and status management")
public class SensorController {

    @Autowired
    private SensorService sensorService;

    @Autowired
    private SensorIngestionService ingestionService;

    // GET all
    @Operation(summary = "Get all sensors")
    @GetMapping("/getAll")
    public List<Sensor> getAll() {
        return sensorService.getAllSensors();
    }

    // POST - Register new hardware
    @Operation(summary = "Register a new sensor")
    @PostMapping("/registerSensor")
    public ResponseEntity<Sensor> register(@RequestBody SensorRegistrationRequest request) {
        return ResponseEntity.ok(sensorService.registerSensor(request));
    }

    // DELETE
    @Operation(summary = "Delete a sensor")
    @DeleteMapping("/{id}/remove")
    public ResponseEntity<String> delete(@PathVariable String id) {
        try {
            sensorService.deleteSensor(id);
            return ResponseEntity.ok("Sensor " + id + " deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting sensor.");
        }
    }

    // PUT - Heartbeat (Battery update)
    @Operation(summary = "Update sensor battery level", description = "Hardware heartbeat with current battery %")
    @PutMapping("/{id}/battery/{level}")
    public ResponseEntity<Sensor> updateBattery(
            @PathVariable String id,
            @PathVariable int level) {
        return ResponseEntity.ok(sensorService.updateBattery(id, level));
    }

    // PUT - Update status
    @Operation(summary = "Update sensor status", description = "Set status to ACTIVE, MALFUNCTION, etc.")
    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<Sensor> updateStatus(
            @PathVariable String id,
            @PathVariable String status) {
        SensorStatus sensorStatus = SensorStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(sensorService.updateStatus(id, sensorStatus));
    }

    // PUT - Assign to Bin
    @Operation(summary = "Assign sensor to a bin")
    @PutMapping("/{id}/assign/{binId}")
    public ResponseEntity<Sensor> assignToBin(
            @PathVariable String id,
            @PathVariable String binId) {
        return ResponseEntity.ok(sensorService.assignToBin(id, binId));
    }


    // Endpoint: PUT /api/sensors/{id}/flag
    // Body: { "flagged": true, "reason": "No readings for 2 days" }
    @Operation(summary = "Flag or unflag a sensor")
    @PutMapping("/{id}/flag")
    public ResponseEntity<Sensor> flagSensor(
            @PathVariable String id,
            @RequestBody SensorFlagRequest request) {

        return ResponseEntity.ok(
                sensorService.setFlag(id, request.isFlagged(), request.getReason())
        );
    }


    // POST /api/sensors/data
    // Hardware sends: { "sensorId": "ESP32-X", "distanceCm": 45.5, "battery": 88 }
    @Operation(summary = "Receive sensor data", description = "IoT hardware posts distance + battery readings")
    @PostMapping("/data")
    public ResponseEntity<String> receiveData(@RequestBody SensorDataRequest request) {
        ingestionService.processSensorData(request);
        return ResponseEntity.ok("Data Processed Successfully");

    }
}