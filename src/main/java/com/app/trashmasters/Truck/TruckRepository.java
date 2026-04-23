package com.app.trashmasters.Truck;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TruckRepository extends MongoRepository<Truck, String> {
    Optional<Truck> findByTruckId(String truckId);


}