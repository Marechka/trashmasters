package com.app.trashmasters.route;

import java.util.List;

public interface RouteService {
    Route generateRouteForDriver(String driverId);
    Route getCurrentRouteForDriver(String driverId);
    Route getRouteById(String id);
    Route completeRoute(String id);
    List<Route> getRoutesByDriver(String driverId);

    // New methods
    Route markBinAsCollected(String routeId, String binId);
    Route markBinAsSkipped(String routeId, String binId);
    Route reportBinIssue(String routeId, String binId, String issueDescription);

    // ✅ Get all routes
    List<Route> getAllRoutes();

    // ✅ Generate routes for all drivers
    List<Route> generateRoutesForAllDrivers(int numDrivers);

    // ✅ NEW: Clear old uncompleted routes before generating new ones
    void clearOldRoutes();

    // ✅ NEW: Get latest route generation session
    List<Route> getLatestRouteGeneration();
}