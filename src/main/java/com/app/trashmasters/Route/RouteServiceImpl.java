package com.app.trashmasters.route;

import com.app.trashmasters.bin.BinRepository;
import com.app.trashmasters.bin.model.Bin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final BinRepository binRepository;

    // Thresholds:
    // If it's physically 70% full -> Pick it up.
    // If ML says it will be 90% full soon -> Pick it up (Optimization).
    private static final int PHYSICAL_THRESHOLD = 70;
    private static final int PREDICTED_THRESHOLD = 90;

    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository, BinRepository binRepository) {
        this.routeRepository = routeRepository;
        this.binRepository = binRepository;
    }

    @Override
    public Route generateRouteForDriver(String driverId) {
        // 1. Get ALL bins so we can filter them with our smart logic
        // (In a huge system, you would write a custom Mongo Query for this, but this is fine for now)
        List<Bin> allBins = binRepository.findAll();

        // 2. Filter: Is it full NOW? Or will it be full SOON?
        List<String> binIdsToPickup = allBins.stream()
                .filter(bin -> {
                    boolean isUrgent = bin.getFillLevel() >= PHYSICAL_THRESHOLD;

                    boolean isPredictedFull = bin.getPredictedFillLevel() != null
                            && bin.getPredictedFillLevel() >= PREDICTED_THRESHOLD;

                    // Logic: Pick it up if it's urgent OR if optimizing prevents a future trip
                    return isUrgent || isPredictedFull;
                })
                .map(Bin::getId)
                .collect(Collectors.toList());

        if (binIdsToPickup.isEmpty()) {
            throw new RuntimeException("No bins need pickup right now (checked real and predicted levels).");
        }

        // 3. Create the Route
        Route newRoute = new Route();
        newRoute.setDriverId(driverId);
        newRoute.setBinIds(binIdsToPickup);
        newRoute.setStatus("CREATED");
        newRoute.setCreatedAt(LocalDateTime.now());
        newRoute.setTotalStops(binIdsToPickup.size());

        return routeRepository.save(newRoute);
    }

    // ... keep the other methods (getRouteById, completeRoute, etc.) the same ...
    @Override
    public Route getRouteById(String id) {
        return routeRepository.findById(id).orElseThrow(() -> new RuntimeException("Route not found"));
    }

    @Override
    public Route completeRoute(String id) {
        Route route = getRouteById(id);
        route.setStatus("COMPLETED");
        return routeRepository.save(route);
    }

    @Override
    public List<Route> getRoutesByDriver(String driverId) {
        return routeRepository.findByDriverIdAndStatus(driverId, "CREATED");
    }
}