package com.app.trashmasters.route;

import java.util.List;

public interface RouteService {
    Route generateRouteForDriver(String driverId);
    Route getRouteById(String id);
    Route completeRoute(String id);
    List<Route> getRoutesByDriver(String driverId);
}