package com.app.trashmasters.sensor;

import com.app.trashmasters.sensor.dto.SensorDataRequest;
import com.app.trashmasters.sensor.dto.SensorFlagRequest;
import com.app.trashmasters.sensor.model.SensorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensors")
@CrossOrigin(origins = "*") // Allow React to access
public class SensorController {

    @Autowired
    private SensorService sensorService;

    @Autowired
    private SensorIngestionService ingestionService;

    // GET all
    @GetMapping
    public List<Sensor> getAll() {
        return sensorService.getAllSensors();
    }

    // POST - Register new hardware
    @PostMapping
    public ResponseEntity<Sensor> register(@RequestBody Map<String, String> payload) {
        String hardwareId = payload.get("assignedId");
        return ResponseEntity.ok(sensorService.registerSensor(hardwareId));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sensorService.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }

    // PUT - Heartbeat (Battery update)
    // Hardware sends: { "level": 85 }
    @PutMapping("/{id}/battery")
    public ResponseEntity<Sensor> updateBattery(
            @PathVariable String id,
            @RequestBody Map<String, Integer> payload) {
        return ResponseEntity.ok(sensorService.updateBattery(id, payload.get("level")));
    }

    // PUT - Manual Flag (e.g., "MALFUNCTION")
    // Payload: { "status": "MALFUNCTION" }
    @PutMapping("/{id}/status")
    public ResponseEntity<Sensor> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        SensorStatus status = SensorStatus.valueOf(payload.get("status"));
        return ResponseEntity.ok(sensorService.updateStatus(id, status));
    }

    // PUT - Assign to Bin
    // Payload: { "binId": "bin_101" }
    @PutMapping("/{id}/assign")
    public ResponseEntity<Sensor> assignToBin(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(sensorService.assignToBin(id, payload.get("binId")));
    }


    // Endpoint: PUT /api/sensors/{id}/flag
    // Body: { "flagged": true, "reason": "No readings for 2 days" }
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
    @PostMapping("/data")
    public ResponseEntity<String> receiveData(@RequestBody SensorDataRequest request) {
        ingestionService.processSensorData(request);
        return ResponseEntity.ok("Data Processed Successfully");
    }
}