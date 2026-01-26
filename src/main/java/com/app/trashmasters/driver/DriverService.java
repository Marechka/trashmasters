package com.trashmasters.project.service;

import com.app.trashmasters.driver.model.Driver;

import java.util.List;

public interface DriverService {
    List<Driver> getAllDrivers();
    Driver getDriverById(String id);
    Driver createDriver(Driver driver);
    Driver updateDriver(String id, Driver driverDetails);
    void deleteDriver(String id);
}