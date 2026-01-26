package com.app.trashmasters.driver;

import com.app.trashmasters.driver.model.Driver;
import com.trashmasters.project.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Autowired
    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public Driver getDriverById(String id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    }

    @Override
    public Driver createDriver(Driver driver) {
        // Business Logic: Check if email already exists
        if (driverRepository.findByEmail(driver.getEmail()).isPresent()) {
            throw new RuntimeException("Driver with this email already exists");
        }
        return driverRepository.save(driver);
    }

    @Override
    public Driver updateDriver(String id, Driver driverDetails) {
        Driver existingDriver = getDriverById(id);

        // Update fields
        existingDriver.setFirstName(driverDetails.getFirstName());
        existingDriver.setLastName(driverDetails.getLastName());
        existingDriver.setPhone(driverDetails.getPhone());
        // Do not update ID or Email if you don't want to allow it

        return driverRepository.save(existingDriver);
    }

    @Override
    public void deleteDriver(String id) {
        Driver driver = getDriverById(id); // Check if exists first
        driverRepository.delete(driver);
    }
}