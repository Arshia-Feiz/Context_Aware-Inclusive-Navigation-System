package com.group2.navigation.repository;

import com.group2.navigation.algorithm.Graph;
import com.group2.navigation.model.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Aggregates all context data repositories and provides high-level methods
 * for the A* algorithm to query safety, lighting, construction, and health data.
 * 
 * Each method converts a lat/lng + radius into a bounding box, then queries
 * the appropriate JPA repository.
 */
@Repository
public class ContextDataRepository {

    @Autowired
    private CrimeIncidentRepository crimeRepo;

    @Autowired
    private StreetLightRepository lightRepo;

    @Autowired
    private ConstructionProjectRepository constructionRepo;

    @Autowired
    private HealthServiceRepository healthRepo;

    @Autowired
    private CyclingSegmentRepository cyclingRepo;

    // Radius (in meters) for each type of spatial query
    private static final double CRIME_RADIUS_M = 500.0;      // 500m for crime score
    private static final double LIGHT_RADIUS_M = 50.0;       // 50m for street lighting
    private static final double CONSTRUCTION_RADIUS_M = 100.0; // 100m for construction
    private static final double HOSPITAL_SEARCH_RADIUS_M = 5000.0; // 5km for hospitals

    // Max crime count used to normalize score to 0-1
    // Based on ~500m radius in downtown Toronto, 50 recent crimes is considered "high"
    private static final double MAX_CRIME_COUNT = 50.0;

    /**
     * Get crime score for a given location.
     * Returns a value 0.0 - 1.0 where 1.0 = very high crime area.
     * 
     * Uses recent crimes (last 3 years) within a 500m radius.
     */
    public double getCrimeScore(double lat, double lng) {
        double[] box = toBoundingBox(lat, lng, CRIME_RADIUS_M);
        int sinceYear = java.time.Year.now().getValue() - 3;

        long count = crimeRepo.countRecentInBoundingBox(
                box[0], box[1], box[2], box[3], sinceYear);

        // Normalize to 0-1 scale
        return Math.min(count / MAX_CRIME_COUNT, 1.0);
    }

    /**
     * Check if a location has street lighting nearby.
     * Returns true if at least one street light pole is within 50m.
     */
    public boolean hasStreetLighting(double lat, double lng) {
        double[] box = toBoundingBox(lat, lng, LIGHT_RADIUS_M);
        long count = lightRepo.countInBoundingBox(box[0], box[1], box[2], box[3]);
        return count > 0;
    }

    /**
     * Check if a location has active construction nearby.
     * Returns true if any active construction project is within 100m.
     */
    public boolean hasConstruction(double lat, double lng) {
        double[] box = toBoundingBox(lat, lng, CONSTRUCTION_RADIUS_M);
        long count = constructionRepo.countActiveInBoundingBox(
                box[0], box[1], box[2], box[3]);
        return count > 0;
    }

    /**
     * Get distance to nearest hospital/health service from a location.
     * Returns distance in meters. Returns Double.MAX_VALUE if none found within 5km.
     */
    public double getDistanceToNearestHospital(double lat, double lng) {
        double[] box = toBoundingBox(lat, lng, HOSPITAL_SEARCH_RADIUS_M);
        List<HealthService> nearby = healthRepo.findInBoundingBox(
                box[0], box[1], box[2], box[3]);

        if (nearby.isEmpty()) {
            return Double.MAX_VALUE;
        }

        double minDist = Double.MAX_VALUE;
        for (HealthService h : nearby) {
            double dist = Graph.haversineDistance(lat, lng, h.getLatitude(), h.getLongitude());
            if (dist < minDist) {
                minDist = dist;
            }
        }

        return minDist;
    }

    /**
     * Check if a location has cycling infrastructure nearby.
     * Returns true if a cycling segment is within 30m.
     */
    public boolean hasCyclingInfrastructure(double lat, double lng) {
        double[] box = toBoundingBox(lat, lng, 30.0);
        long count = cyclingRepo.countInBoundingBox(box[0], box[1], box[2], box[3]);
        return count > 0;
    }

    // =========================================================================
    // Helper: convert a point + radius to a lat/lng bounding box
    // =========================================================================

    /**
     * Convert a center point and radius (meters) into a bounding box.
     * 
     * @return double[] {minLat, maxLat, minLng, maxLng}
     */
    private double[] toBoundingBox(double lat, double lng, double radiusMeters) {
        // 1 degree latitude ≈ 111,000 meters
        double latOffset = radiusMeters / 111_000.0;

        // 1 degree longitude depends on latitude
        double lngOffset = radiusMeters / (111_000.0 * Math.cos(Math.toRadians(lat)));

        return new double[]{
                lat - latOffset, // minLat
                lat + latOffset, // maxLat
                lng - lngOffset, // minLng
                lng + lngOffset  // maxLng
        };
    }
}
