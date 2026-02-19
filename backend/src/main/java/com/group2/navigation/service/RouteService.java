package com.group2.navigation.service;

import com.group2.navigation.algorithm.AStarRouter;
import com.group2.navigation.algorithm.Graph;
import com.group2.navigation.algorithm.Node;
import com.group2.navigation.model.RouteRequest;
import com.group2.navigation.model.RouteResponse;
import com.group2.navigation.repository.ContextDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for calculating routes.
 * Wires together the graph, A* router, and context data for live queries.
 */
@Service
public class RouteService {

    @Autowired
    private GraphService graphService;

    @Autowired
    private ContextDataRepository contextRepo;

    private AStarRouter router;

    @PostConstruct
    public void init() {
        // Initialize router with the graph AND context data for live DB queries
        Graph graph = graphService.getGraph();
        this.router = new AStarRouter(graph, contextRepo);
    }

    /**
     * Calculate a route based on the request.
     */
    public RouteResponse calculateRoute(RouteRequest request) {
        // Find route using A* with context-aware edge weighting
        List<Node> path = router.findRoute(
            request.getStartLat(),
            request.getStartLng(),
            request.getEndLat(),
            request.getEndLng(),
            request.getPreferences()
        );

        if (path.isEmpty()) {
            return RouteResponse.error("No route found between the given points");
        }

        // Convert path to coordinate list for frontend
        List<double[]> coordinates = new ArrayList<>();
        for (Node node : path) {
            coordinates.add(new double[]{node.getLat(), node.getLng()});
        }

        // Calculate stats
        double distance = router.calculatePathDistance(path);
        double time = router.estimateWalkingTime(path);

        return new RouteResponse(coordinates, distance, time);
    }
}
