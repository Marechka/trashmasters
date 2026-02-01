package com.app.trashmasters.route;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {
    // Find active routes for a specific driver
    List<Route> findByDriverIdAndStatus(String driverId, String status);
}