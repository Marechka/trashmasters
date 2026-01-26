package com.app.trashmasters.driver;

import com.app.trashmasters.driver.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {
    // Custom query to find a driver by their email (useful for login/uniqueness check)
    Optional<Driver> findByEmail(String email);
}