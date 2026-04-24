package com.app.trashmasters.route;

import com.app.trashmasters.bin.BinRepository;
import com.app.trashmasters.bin.model.Bin;
import com.app.trashmasters.employee.EmployeeRepository;
import com.app.trashmasters.employee.model.Employee;
import com.app.trashmasters.employee.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final BinRepository binRepository;
    private final EmployeeRepository employeeRepository;

    private static final int PHYSICAL_THRESHOLD = 70;
    private static final int PREDICTED_THRESHOLD = 90;
    private static final double EARTH_RADIUS_KM = 6371.0;

    @Autowired
    public RouteServiceImpl(
            RouteRepository routeRepository,
            BinRepository binRepository,
            EmployeeRepository employeeRepository
    ) {
        this.routeRepository = routeRepository;
        this.binRepository = binRepository;
        this.employeeRepository = employeeRepository;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c * 0.621371;
    }

    private List<String> optimizeRoute(List<Bin> bins) {
        if (bins == null || bins.isEmpty()) return new ArrayList<>();

        List<String> route = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        String currentBin = bins.get(0).getId();
        route.add(currentBin);
        visited.add(currentBin);

        while (visited.size() < bins.size()) {
            String nearestBin = null;
            double minDistance = Double.MAX_VALUE;

            for (Bin bin : bins) {
                String binId = bin.getId();
                if (!visited.contains(binId)) {
                    String finalCurrentBin = currentBin;
                    Bin current = bins.stream()
                            .filter(b -> b.getId().equals(finalCurrentBin))
                            .findFirst()
                            .orElse(bins.get(0));

                    double distance = calculateDistance(
                            current.getLatitude(), current.getLongitude(),
                            bin.getLatitude(), bin.getLongitude()
                    );

                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestBin = binId;
                    }
                }
            }

            if (nearestBin != null) {
                route.add(nearestBin);
                visited.add(nearestBin);
                currentBin = nearestBin;
            }
        }

        return route;
    }

    private Map<String, List<Bin>> assignBinsToDrivers(List<Bin> binsNeedingPickup, List<Employee> drivers) {
        Map<String, List<Bin>> driverAssignments = new HashMap<>();
        for (Employee driver : drivers) {
            driverAssignments.put(driver.getEmployeeId(), new ArrayList<>());
        }

        if (binsNeedingPickup == null || binsNeedingPickup.isEmpty()) {
            return driverAssignments;
        }

        final List<Bin> sortedBins = binsNeedingPickup.stream()
                .sorted((b1, b2) -> Double.compare(b2.getFillLevel(), b1.getFillLevel()))
                .collect(Collectors.toList());

        final List<String> driverIds = new ArrayList<>(driverAssignments.keySet());
        final int[] driverLoad = new int[drivers.size()];

        for (Bin bin : sortedBins) {
            int bestDriver = 0;
            int minLoad = driverLoad[0];
            for (int i = 1; i < drivers.size(); i++) {
                if (driverLoad[i] < minLoad) {
                    minLoad = driverLoad[i];
                    bestDriver = i;
                }
            }
            String bestDriverId = driverIds.get(bestDriver);
            driverAssignments.get(bestDriverId).add(bin);
            driverLoad[bestDriver]++;
        }

        return driverAssignments;
    }

    @Override
    public Route generateRouteForDriver(String driverId) {
        List<Bin> allBins = binRepository.findAll();

        List<String> binIdsToPickup = allBins.stream()
                .filter(bin -> {
                    boolean isUrgent = bin.getFillLevel() >= PHYSICAL_THRESHOLD;
                    boolean isPredictedFull = bin.getPredictedFillLevel() != null
                            && bin.getPredictedFillLevel() >= PREDICTED_THRESHOLD;
                    return isUrgent || isPredictedFull;
                })
                .map(Bin::getId)
                .toList();

        if (binIdsToPickup.isEmpty()) {
            throw new RuntimeException("No bins need pickup right now");
        }

        Route newRoute = new Route();
        newRoute.setRouteNumber("R-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        newRoute.setDriverId(driverId);
        newRoute.setBinIds(binIdsToPickup);
        newRoute.setCompletedBinIds(new ArrayList<>());
        newRoute.setCurrentStopIndex(0);
        newRoute.setStatus("CREATED");
        newRoute.setCreatedAt(LocalDateTime.now());
        newRoute.setTotalStops(binIdsToPickup.size());
        newRoute.setTotalDistance((double) (binIdsToPickup.size() * 1.5));
        newRoute.setEstimatedTime(binIdsToPickup.size() * 5);
        newRoute.setGenerationSession(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")));

        return routeRepository.save(newRoute);
    }

    @Override
    public List<Route> generateRoutesForAllDrivers(int numDrivers) {
        System.out.println("=== GENERATING ROUTES FOR " + numDrivers + " DRIVERS ===");

        clearOldRoutes();

        if (numDrivers < 1 || numDrivers > 9) {
            throw new RuntimeException("Number of drivers must be between 1 and 9");
        }

        List<Employee> drivers = employeeRepository.findByRole(UserRole.DRIVER);
        System.out.println("Found " + drivers.size() + " drivers in database");

        int actualDrivers = Math.min(numDrivers, drivers.size());

        if (actualDrivers == 0) {
            throw new RuntimeException("No drivers available in database");
        }

        List<Bin> allBins = binRepository.findAll();
        System.out.println("Total bins in database: " + allBins.size());

        final List<Bin> binsNeedingPickup = allBins.stream()
                .filter(bin -> bin.getFillLevel() >= PHYSICAL_THRESHOLD ||
                        (bin.getPredictedFillLevel() != null &&
                                bin.getPredictedFillLevel() >= PREDICTED_THRESHOLD))
                .collect(Collectors.toList());

        System.out.println("Bins needing pickup: " + binsNeedingPickup.size());

        if (binsNeedingPickup.isEmpty()) {
            throw new RuntimeException("No bins need pickup (fill level >= 70%)");
        }

        String sessionId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        System.out.println("Generation Session: " + sessionId);

        Map<String, List<Bin>> driverAssignments = assignBinsToDrivers(
                binsNeedingPickup,
                drivers.subList(0, actualDrivers)
        );

        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < actualDrivers; i++) {
            Employee driver = drivers.get(i);
            List<Bin> assignedBins = driverAssignments.get(driver.getEmployeeId());

            System.out.println("Driver " + driver.getEmployeeId() + " has " +
                    (assignedBins != null ? assignedBins.size() : 0) + " bins assigned");

            if (assignedBins == null || assignedBins.isEmpty()) {
                continue;
            }

            List<String> optimizedBinIds = optimizeRoute(assignedBins);

            Route route = new Route();
            route.setRouteNumber("R-" + (i + 1) + "-" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            route.setDriverId(driver.getEmployeeId());
            route.setDriverName(driver.getFirstName() + " " + driver.getLastName());
            route.setBinIds(optimizedBinIds);
            route.setCompletedBinIds(new ArrayList<>());
            route.setCurrentStopIndex(0);
            route.setStatus("CREATED");
            route.setCreatedAt(LocalDateTime.now());
            route.setTotalStops(optimizedBinIds.size());
            route.setGenerationSession(sessionId);

            double distance = calculateRouteDistance(assignedBins);
            int time = calculateRouteTime(assignedBins);

            route.setTotalDistance(distance);
            route.setEstimatedTime(time);

            routes.add(routeRepository.save(route));
            System.out.println("Created route for driver " + driver.getEmployeeId() + ": " + route.getRouteNumber());
        }

        System.out.println("Total routes created: " + routes.size());
        return routes;
    }

    @Override
    public void clearOldRoutes() {
        List<Route> allRoutes = routeRepository.findAll();
        for (Route route : allRoutes) {
            if ("CREATED".equals(route.getStatus())) {
                routeRepository.delete(route);
                System.out.println("Cleared old route: " + route.getRouteNumber());
            }
        }
    }

    @Override
    public List<Route> getLatestRouteGeneration() {
        List<Route> allRoutes = routeRepository.findAll();
        if (allRoutes.isEmpty()) return new ArrayList<>();

        String latestSession = allRoutes.stream()
                .map(Route::getGenerationSession)
                .filter(Objects::nonNull)
                .max(String::compareTo)
                .orElse("");

        return allRoutes.stream()
                .filter(route -> latestSession.equals(route.getGenerationSession()))
                .collect(Collectors.toList());
    }

    private double calculateRouteDistance(List<Bin> bins) {
        if (bins == null || bins.size() < 2) return 0.0;
        double total = 0;
        for (int i = 0; i < bins.size() - 1; i++) {
            total += calculateDistance(
                    bins.get(i).getLatitude(), bins.get(i).getLongitude(),
                    bins.get(i + 1).getLatitude(), bins.get(i + 1).getLongitude()
            );
        }
        return total;
    }

    private int calculateRouteTime(List<Bin> bins) {
        if (bins == null || bins.isEmpty()) return 0;
        return bins.size() * 5 + (int)(calculateRouteDistance(bins) / 30 * 60);
    }

    @Override
    public Route getCurrentRouteForDriver(String driverId) {
        List<String> validStatuses = Arrays.asList("CREATED", "IN PROGRESS", "COMPLETED");
        List<Route> activeRoutes = routeRepository.findByDriverIdAndStatusIn(driverId, validStatuses);

        if (activeRoutes.isEmpty()) {
            throw new RuntimeException("No active route found for driver");
        }

        // Optional: Sort by status priority if needed
        // Priority: CREATED > IN_PROGRESS > COMPLETED
        return activeRoutes.get(0);
    }

    @Override
    public Route markBinAsCollected(String routeId, String binId) {
        Route route = getRouteById(routeId);

        if (!route.getBinIds().contains(binId)) {
            throw new RuntimeException("Bin not in route");
        }
        if (route.getCompletedBinIds() == null) {
            route.setCompletedBinIds(new ArrayList<>());
        }
        if (!route.getCompletedBinIds().contains(binId)) {
            route.getCompletedBinIds().add(binId);
        }

        route.setCurrentStopIndex(route.getCompletedBinIds().size());

        // Check if ALL bins are collected
        int totalStops = route.getBinIds().size();
        int completedStops = route.getCompletedBinIds().size();

        if (completedStops >= totalStops) {
            route.setStatus("COMPLETED");
            route.setCompletedAt(LocalDateTime.now());
        } else {
            route.setStatus("IN PROGRESS");
        }

        Bin bin = binRepository.findById(binId)
                .orElseThrow(() -> new RuntimeException("Bin not found"));
        bin.setFillLevel(0.0);
        bin.setLastCollected(LocalDateTime.now());
        binRepository.save(bin);

        return routeRepository.save(route);
    }

    @Override
    public Route markBinAsSkipped(String routeId, String binId) {
        Route route = getRouteById(routeId);
        if (route.getCompletedBinIds() == null) {
            route.setCompletedBinIds(new ArrayList<>());
        }
        if (!route.getCompletedBinIds().contains(binId)) {
            route.getCompletedBinIds().add(binId);
        }
        route.setCurrentStopIndex(route.getCompletedBinIds().size());
        if (route.getCompletedBinIds().size() >= route.getTotalStops()) {
            route.setStatus("COMPLETED");
            route.setCompletedAt(LocalDateTime.now());
        } else {
            route.setStatus("IN PROGRESS");
        }
        return routeRepository.save(route);
    }

    @Override
    public Route reportBinIssue(String routeId, String binId, String issueDescription) {
        Route route = getRouteById(routeId);
        System.out.println("Issue reported for bin " + binId + ": " + issueDescription);
        return routeRepository.save(route);
    }

    @Override
    public Route getRouteById(String id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));
    }

    @Override
    public Route completeRoute(String id) {
        Route route = getRouteById(id);
        route.setStatus("COMPLETED");
        route.setCompletedAt(LocalDateTime.now());
        return routeRepository.save(route);
    }

    @Override
    public List<Route> getRoutesByDriver(String driverId) {
        return routeRepository.findByDriverIdAndStatus(driverId, "CREATED");
    }

    @Override
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
}
