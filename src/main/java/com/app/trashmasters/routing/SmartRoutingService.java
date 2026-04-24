package com.app.trashmasters.routing;


import com.app.trashmasters.route.RouteDTO;
import com.app.trashmasters.route.RouteStepDTO;
import com.app.trashmasters.Truck.Truck;
import com.app.trashmasters.bin.model.Bin;
import com.app.trashmasters.bin.model.Location;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure VRP solver — takes a time matrix, bins, and trucks and returns optimized routes.
 * Has NO business logic, NO database access. That belongs in RouteServiceImpl.
 */
@Service
public class SmartRoutingService {

    static {
        Loader.loadNativeLibraries();
    }

    /**
     * Solves the Vehicle Routing Problem using Google OR-Tools.
     * Bins are expected to be pre-sorted by priority (highest priority first)
     * so the solver's drop penalties align with business importance.
     */
    public List<RouteDTO> generateRoutes(
            long[][] timeMatrix,
            List<Bin> targetBins,
            List<Truck> trucks,
            Location stationA,
            Location dumpB) {

        int vehicleNumber = trucks.size();

        System.out.println("⚙️ Initializing OR-Tools VRP Solver...");
        try {
            int[] starts = new int[vehicleNumber];
            int[] ends = new int[vehicleNumber];
            for (int i = 0; i < vehicleNumber; i++) {
                starts[i] = 0; // Station A (Node 0)
                ends[i] = 0;   // Return to Station A
            }

            RoutingIndexManager manager = new RoutingIndexManager(
                    timeMatrix.length, vehicleNumber, starts, ends);
            RoutingModel routing = new RoutingModel(manager);

            // Time/Distance Callback (+5 min service time per bin)
            final int transitCallbackIndex = routing.registerTransitCallback(
                    (long fromIndex, long toIndex) -> {
                        int fromNode = manager.indexToNode(fromIndex);
                        int toNode = manager.indexToNode(toIndex);
                        long serviceTime = (fromNode > 1) ? 5 : 0;
                        return timeMatrix[fromNode][toNode] + serviceTime;
                    });
            routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

            // 8-Hour Shift Constraint
            routing.addDimension(transitCallbackIndex,
                    0, 480, true, "Time");

            // Capacity Callback (compacted yards per bin)
            final int demandCallbackIndex = routing.registerUnaryTransitCallback(
                    (long fromIndex) -> {
                        int fromNode = manager.indexToNode(fromIndex);
                        if (fromNode == 0 || fromNode == 1) return 0;

                        Bin bin = targetBins.get(fromNode - 2);
                        double currentFill = (bin.getFillLevel() != null) ? bin.getFillLevel() : 0.0;
                        double futureFill = (bin.getFuturePredictions() != null)
                                ? bin.getFuturePredictions().getOrDefault(8, 0.0) : 0.0;
                        double fillPercent = Math.max(currentFill, futureFill) / 100.0;

                        Integer capacity = bin.getCapacityYards();
                        if (capacity == null) {
                            throw new IllegalArgumentException(
                                    "Bin " + bin.getBinId() + " is missing capacityYards.");
                        }
                        return Math.round((capacity * fillPercent) / 4.0);
                    });

            // Vehicle capacities and starting loads
            long[] vehicleCapacities = new long[vehicleNumber];
            long[] vehicleStartingLoads = new long[vehicleNumber];
            for (int i = 0; i < vehicleNumber; i++) {
                vehicleCapacities[i] = 30;
                Double truckYards = trucks.get(i).getCurrentCompactedYards();
                vehicleStartingLoads[i] = truckYards != null ? Math.round(truckYards) : 0L;
            }

            routing.addDimensionWithVehicleCapacity(
                    demandCallbackIndex, 0, vehicleCapacities, false, "Capacity");

            RoutingDimension capacityDimension = routing.getMutableDimension("Capacity");
            for (int i = 0; i < vehicleNumber; ++i) {
                long index = routing.start(i);
                capacityDimension.cumulVar(index).setValue(vehicleStartingLoads[i]);
            }

            // Penalties for dropping bins — uses priority score passed from RouteServiceImpl
            // Higher-priority bins (commercial, overdue, nearly full) get higher penalties
            for (int i = 2; i < timeMatrix.length; ++i) {
                Bin bin = targetBins.get(i - 2);
                long penalty = calculateDropPenalty(bin);
                routing.addDisjunction(new long[]{manager.nodeToIndex(i)}, penalty);
            }

            // Solve
            RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
                    .toBuilder()
                    .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                    .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                    .setTimeLimit(com.google.protobuf.Duration.newBuilder().setSeconds(5).build())
                    .build();

            Assignment solution = routing.solveWithParameters(searchParameters);

            return extractRoutes(manager, routing, solution, vehicleNumber,
                    targetBins, trucks, stationA, dumpB);

        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println("🚨 DATA ERROR: " + e.getMessage());
            throw new RuntimeException("Data integrity failure in routing engine: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            System.err.println("🚨 SOLVER ERROR: " + e.getMessage());
            throw new RuntimeException("OR-Tools Solver Crash: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("🚨 UNKNOWN ERROR: " + e.getMessage());
            throw new RuntimeException("Unexpected error in SmartRoutingService: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates the OR-Tools drop penalty for a bin based on its priority attributes.
     * Higher penalty = solver tries harder to include this bin on a route.
     *
     *  Base: 1,000
     *  Overdue: +1,000,000 per day overdue
     *  Zone:   COMMERCIAL +500,000  |  PUBLIC +200,000  |  RESIDENTIAL +0
     *  Fill:   >90% adds +300,000 (overflow risk)
     */
    private long calculateDropPenalty(Bin bin) {
        long penalty = 1000L;

        // Overdue bins are top priority — escalates each day
        int overdue = (bin.getDaysOverdue() != null) ? bin.getDaysOverdue() : 0;
        penalty += overdue * 1_000_000L;

        // Zone-based priority
        if (bin.getZone() != null) {
            switch (bin.getZone()) {
                case COMMERCIAL: penalty += 500_000L; break;
                case PUBLIC:     penalty += 200_000L; break;
                default:         break; // RESIDENTIAL = base
            }
        }

        // Near-overflow urgency
        double fill = (bin.getFillLevel() != null) ? bin.getFillLevel() : 0.0;
        if (fill >= 90.0) {
            penalty += 300_000L;
        }

        return penalty;
    }

    /**
     * Extracts the optimized routes from the OR-Tools solution into RouteDTOs.
     */
    private List<RouteDTO> extractRoutes(
            RoutingIndexManager manager,
            RoutingModel routing,
            Assignment solution,
            int vehicleNumber,
            List<Bin> targetBins,
            List<Truck> trucks,
            Location stationA,
            Location dumpB) {

        List<RouteDTO> generatedRoutes = new ArrayList<>();
        RoutingDimension timeDimension = routing.getMutableDimension("Time");
        RoutingDimension capacityDimension = routing.getMutableDimension("Capacity");
        Set<String> assignedBinIds = new HashSet<>();

        for (int vehicleId = 0; vehicleId < vehicleNumber; ++vehicleId) {

            long index = routing.start(vehicleId);

            RouteDTO routeDTO = new RouteDTO();
            routeDTO.setTruckId(trucks.get(vehicleId).getTruckId());
            routeDTO.setDriverId(trucks.get(vehicleId).getAssignedDriverId());

            List<RouteStepDTO> steps = new ArrayList<>();
            long currentVolume = solution.value(capacityDimension.cumulVar(index));

            steps.add(new RouteStepDTO(
                    stationA.getLat(), stationA.getLon(), "STATION", null, "START", 0));

            while (!routing.isEnd(index)) {
                index = solution.value(routing.nextVar(index));
                int nodeIndex = manager.indexToNode(index);
                long etaMinutes = solution.value(timeDimension.cumulVar(index));

                if (nodeIndex == 0) {
                    steps.add(new RouteStepDTO(
                            stationA.getLat(), stationA.getLon(), "STATION", null, "END", etaMinutes));
                } else if (nodeIndex == 1) {
                    steps.add(new RouteStepDTO(
                            dumpB.getLat(), dumpB.getLon(), "DUMP", null, "EMPTY_TRUCK", etaMinutes));
                    currentVolume = 0;
                } else {
                    Bin bin = targetBins.get(nodeIndex - 2);
                    steps.add(new RouteStepDTO(
                            bin.getLocation().getLat(), bin.getLocation().getLon(),
                            "BIN", bin.getBinId(), "PICKUP", etaMinutes));
                    assignedBinIds.add(bin.getBinId());
                    currentVolume = solution.value(capacityDimension.cumulVar(index));
                }
            }

            long totalShiftTime = solution.value(timeDimension.cumulVar(routing.end(vehicleId)));
            routeDTO.setTotalTimeMinutes(totalShiftTime);
            routeDTO.setEndingTruckVolumeYards(currentVolume);
            routeDTO.setSteps(steps);
            generatedRoutes.add(routeDTO);
        }

        // Log skipped bins
        long skippedCount = targetBins.stream()
                .filter(b -> !assignedBinIds.contains(b.getBinId()))
                .count();

        System.out.println("🚛 Routes Generated: " + generatedRoutes.size());
        System.out.println("⚠️ Bins Skipped (Priority for tomorrow): " + skippedCount);

        return generatedRoutes;
    }
}

