package com.app.trashmasters.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "http://localhost:3000")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // POST - Generate a new route for a specific driver
    // URL: /api/routes/generate?driverId=12345
    @PostMapping("/generate")
    public ResponseEntity<Route> generateRoute(@RequestParam String driverId) {
        return ResponseEntity.ok(routeService.generateRouteForDriver(driverId));
    }

    // GET - Get a specific route
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable String id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    // PUT - Mark route as complete
    @PutMapping("/{id}/complete")
    public ResponseEntity<Route> completeRoute(@PathVariable String id) {
        return ResponseEntity.ok(routeService.completeRoute(id));
    }
}