package com.app.trashmasters.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // Renamed path segment from /driver/ to /by-driver/ to eliminate
    // any ambiguity with the /{id} wildcard. Spring MVC should prefer specific
    // paths over wildcards, but with hyphenated path variables like EMP-123-456
    // some versions of Spring/Tomcat misroute the request, causing a 405.
    // The frontend DriverPage.jsx must also be updated to call /by-driver/.
    @GetMapping("/by-driver/{driverId}/current")
    public ResponseEntity<Route> getCurrentRoute(@PathVariable String driverId) {
        try {
            Route route = routeService.getCurrentRouteForDriver(driverId);
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-driver/{driverId}")
    public ResponseEntity<List<Route>> getRoutesByDriver(@PathVariable String driverId) {
        return ResponseEntity.ok(routeService.getRoutesByDriver(driverId));
    }

    // GET latest route generation (for AdminRoutePlanner)
    @GetMapping("/latest")
    public ResponseEntity<List<Route>> getLatestGeneration() {
        return ResponseEntity.ok(routeService.getLatestRouteGeneration());
    }

    // GET route by MongoDB id — keep LAST to avoid swallowing other paths
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(routeService.getRouteById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET all routes
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    // POST generate route for a single driver
    @PostMapping("/generate")
    public ResponseEntity<?> generateRoute(@RequestParam String driverId) {
        try {
            Route route = routeService.generateRouteForDriver(driverId);
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // POST generate routes for all drivers
    @PostMapping("/generate-all")
    public ResponseEntity<?> generateAllRoutes(@RequestParam(defaultValue = "6") int numDrivers) {
        try {
            int actualDrivers = Math.max(1, Math.min(9, numDrivers));
            List<Route> routes = routeService.generateRoutesForAllDrivers(actualDrivers);
            return ResponseEntity.ok(routes);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{routeId}/bins/{binId}/collect")
    public ResponseEntity<Route> collectBin(
            @PathVariable String routeId,
            @PathVariable String binId) {
        try {
            Route route = routeService.markBinAsCollected(routeId, binId);
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{routeId}/bins/{binId}/skip")
    public ResponseEntity<Route> skipBin(
            @PathVariable String routeId,
            @PathVariable String binId) {
        try {
            Route route = routeService.markBinAsSkipped(routeId, binId);
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{routeId}/bins/{binId}/report-issue")
    public ResponseEntity<Route> reportIssue(
            @PathVariable String routeId,
            @PathVariable String binId,
            @RequestBody Map<String, String> issue) {
        try {
            Route route = routeService.reportBinIssue(routeId, binId, issue.get("description"));
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Route> completeRoute(@PathVariable String id) {
        try {
            return ResponseEntity.ok(routeService.completeRoute(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
