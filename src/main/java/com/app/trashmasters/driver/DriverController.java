package com.app.trashmasters.driver;

import com.app.trashmasters.driver.dto.DriverRequest;
import com.app.trashmasters.driver.model.Driver;
import com.trashmasters.project.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend to access this
public class DriverController {

    private final DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // GET /api/drivers
    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    // GET /api/drivers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable String id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    // POST /api/drivers
    @PostMapping("/createDriver")
    public ResponseEntity<Driver> createDriver(@RequestBody DriverRequest driverRequest) {
        // Map DTO to Entity
        Driver newDriver = new Driver();
        newDriver.setFirstName(driverRequest.getFirstName());
        newDriver.setLastName(driverRequest.getLastName());
        newDriver.setEmail(driverRequest.getEmail());
        newDriver.setPhone(driverRequest.getPhone());

        Driver savedDriver = driverService.createDriver(newDriver);
        return new ResponseEntity<>(savedDriver, HttpStatus.CREATED);
    }

    // PUT /api/drivers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable String id, @RequestBody DriverRequest driverRequest) {
        // Map DTO to Entity
        Driver driverUpdates = new Driver();
        driverUpdates.setFirstName(driverRequest.getFirstName());
        driverUpdates.setLastName(driverRequest.getLastName());
        driverUpdates.setPhone(driverRequest.getPhone());

        return ResponseEntity.ok(driverService.updateDriver(id, driverUpdates));
    }

    // DELETE /api/drivers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable String id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}