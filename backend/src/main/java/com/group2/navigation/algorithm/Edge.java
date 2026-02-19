package com.group2.navigation.algorithm;

import com.group2.navigation.model.UserPreferences;
import com.group2.navigation.repository.ContextDataRepository;

/**
 * Represents an edge (road segment) in the road network graph.
 * The weighted cost calculation is the core of our context-aware routing.
 */
public class Edge {

    private Node source;
    private Node target;
    private double distance; // Base distance in meters

    // Context attributes (from OSM and city data)
    private boolean wheelchairAccessible;
    private boolean lit;           // Has street lighting
    private double crimeScore;     // 0-1, higher = more crime
    private boolean hasConstruction;
    private String surfaceType;    // paved, gravel, etc.

    public Edge(Node source, Node target, double distance) {
        this.source = source;
        this.target = target;
        this.distance = distance;

        // Default values
        this.wheelchairAccessible = true;
        this.lit = true;
        this.crimeScore = 0;
        this.hasConstruction = false;
        this.surfaceType = "paved";
    }

    /**
     * Calculate the weighted cost of this edge based on user preferences.
     * This is where context-aware routing happens.
     * 
     * The formula:
     *   cost = baseDistance
     *        + (crimeScore * CRIME_WEIGHT)     [if user avoids crime]
     *        + (UNLIT_PENALTY)                  [if user prefers lit streets & edge is unlit]
     *        + (CONSTRUCTION_PENALTY)           [if user avoids construction & edge has it]
     *        + (HOSPITAL_PENALTY)               [if user wants nearby hospital & edge is far]
     *        = INFINITY                         [if wheelchair needed but edge not accessible]
     * 
     * @param prefs user's routing preferences
     */
    public double getWeightedCost(UserPreferences prefs) {
        double weight = distance;

        // Hard constraint: wheelchair accessibility
        // If user needs wheelchair access and edge doesn't support it, block this edge
        if (prefs.isWheelchairAccessible() && !wheelchairAccessible) {
            return Double.MAX_VALUE;
        }

        // Soft constraint: crime avoidance
        // Adds up to 1000m equivalent penalty for high-crime areas
        if (prefs.isAvoidHighCrime()) {
            weight += crimeScore * 1000.0;
        }

        // Soft constraint: prefer lit streets
        // At night (after 8pm or before 6am), penalize unlit streets more heavily
        if (prefs.isPreferLitStreets() && !lit) {
            int hour = prefs.getTimeOfDay();
            if (hour >= 20 || hour < 6) {
                // Nighttime: heavy penalty for unlit streets
                weight += 500.0;
            } else {
                // Daytime: small penalty (still slightly prefer lit streets)
                weight += 50.0;
            }
        }

        // Soft constraint: avoid construction
        // Adds a 800m equivalent penalty for construction zones
        if (prefs.isAvoidConstruction() && hasConstruction) {
            weight += 800.0;
        }

        // Soft constraint: stay near hospitals
        // If maxDistanceToHospital > 0, user wants to be within that distance
        if (prefs.getMaxDistanceToHospital() > 0) {
            // We'd need the hospital distance here -- for now this is handled
            // in the context overlay when building the graph.
            // The actual check happens during graph construction by the GraphService.
        }

        return weight;
    }

    /**
     * Alternative weighted cost that queries the database in real-time.
     * Use this when context data hasn't been pre-loaded onto edges.
     * 
     * @param prefs user's routing preferences
     * @param contextRepo repository for live context data queries
     */
    public double getWeightedCost(UserPreferences prefs, ContextDataRepository contextRepo) {
        double weight = distance;

        // Hard constraint: wheelchair
        if (prefs.isWheelchairAccessible() && !wheelchairAccessible) {
            return Double.MAX_VALUE;
        }

        // Get the midpoint of this edge for context lookups
        double midLat = (source.getLat() + target.getLat()) / 2.0;
        double midLng = (source.getLng() + target.getLng()) / 2.0;

        // Crime avoidance
        if (prefs.isAvoidHighCrime()) {
            double liveCrimeScore = contextRepo.getCrimeScore(midLat, midLng);
            weight += liveCrimeScore * 1000.0;
        }

        // Street lighting
        if (prefs.isPreferLitStreets()) {
            boolean hasLight = contextRepo.hasStreetLighting(midLat, midLng);
            if (!hasLight) {
                int hour = prefs.getTimeOfDay();
                weight += (hour >= 20 || hour < 6) ? 500.0 : 50.0;
            }
        }

        // Construction
        if (prefs.isAvoidConstruction()) {
            boolean nearConstruction = contextRepo.hasConstruction(midLat, midLng);
            if (nearConstruction) {
                weight += 800.0;
            }
        }

        // Hospital proximity
        if (prefs.getMaxDistanceToHospital() > 0) {
            double hospitalDist = contextRepo.getDistanceToNearestHospital(midLat, midLng);
            if (hospitalDist > prefs.getMaxDistanceToHospital()) {
                // Penalty proportional to how far we are beyond the limit
                double excess = hospitalDist - prefs.getMaxDistanceToHospital();
                weight += excess * 0.5;
            }
        }

        return weight;
    }

    // Getters and Setters
    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }

    public double getCrimeScore() {
        return crimeScore;
    }

    public void setCrimeScore(double crimeScore) {
        this.crimeScore = crimeScore;
    }

    public boolean hasConstruction() {
        return hasConstruction;
    }

    public void setHasConstruction(boolean hasConstruction) {
        this.hasConstruction = hasConstruction;
    }

    public String getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(String surfaceType) {
        this.surfaceType = surfaceType;
    }
}
