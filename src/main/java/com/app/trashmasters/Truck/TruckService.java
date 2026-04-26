package com.app.trashmasters.Truck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TruckService {

    @Autowired
    private TruckRepository truckRepository;

    // 1. CREATE TRUCK
    public Truck createTruck(Truck truck) {
        // Enforce the 30-yard physical limit right away
        if (truck.getCurrentCompactedYards() != null && truck.getCurrentCompactedYards() > 30.0) {
            throw new IllegalArgumentException("A truck cannot hold more than 30 compacted yards of trash.");
        }
        return truckRepository.save(truck);
    }

    // 2. GET ALL TRUCKS
    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    // 3. UPDATE TRUCK
    public Truck updateTruck(String truckId, Truck updatedTruckData) {
        Optional<Truck> existingTruckOpt = truckRepository.findByTruckId(truckId);

        if (existingTruckOpt.isPresent()) {
            Truck existingTruck = existingTruckOpt.get();

            // Update fields if they are provided in the request
            if (updatedTruckData.getAssignedDriverId() != null) {
                existingTruck.setAssignedDriverId(updatedTruckData.getAssignedDriverId());
            }
            if (updatedTruckData.getCurrentCompactedYards() != null) {
                if (updatedTruckData.getCurrentCompactedYards() > 30.0) {
                    throw new IllegalArgumentException("A truck cannot hold more than 30 compacted yards of trash.");
                }
                existingTruck.setCurrentCompactedYards(updatedTruckData.getCurrentCompactedYards());
            }

            return truckRepository.save(existingTruck);
        } else {
            throw new RuntimeException("Truck not found with ID: " + truckId);
        }
    }

    // 4. DELETE TRUCK
    public void deleteTruck(String truckId) {
        Optional<Truck> existingTruckOpt = truckRepository.findByTruckId(truckId);
        if (existingTruckOpt.isPresent()) {
            truckRepository.delete(existingTruckOpt.get());
        } else {
            throw new RuntimeException("Cannot delete. Truck not found with ID: " + truckId);
        }
    }
}