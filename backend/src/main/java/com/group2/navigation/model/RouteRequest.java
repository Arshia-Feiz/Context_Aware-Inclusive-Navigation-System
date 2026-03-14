package com.group2.navigation.model;

import com.group2.navigation.validation.ValidRouteRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

/**
 * Request body for route calculation endpoint.
 */
@ValidRouteRequest
public class RouteRequest {

    @DecimalMin(value = "-90", message = "startLat must be between -90 and 90")
    @DecimalMax(value = "90", message = "startLat must be between -90 and 90")
    private double startLat;

    @DecimalMin(value = "-180", message = "startLng must be between -180 and 180")
    @DecimalMax(value = "180", message = "startLng must be between -180 and 180")
    private double startLng;

    @DecimalMin(value = "-90", message = "endLat must be between -90 and 90")
    @DecimalMax(value = "90", message = "endLat must be between -90 and 90")
    private double endLat;

    @DecimalMin(value = "-180", message = "endLng must be between -180 and 180")
    @DecimalMax(value = "180", message = "endLng must be between -180 and 180")
    private double endLng;

    private String startAddress;
    private String endAddress;

    @Valid
    private UserPreferences preferences;

    // Constructors
    public RouteRequest() {}

    public RouteRequest(double startLat, double startLng, double endLat, double endLng) {
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
        this.preferences = new UserPreferences();
    }

    // Getters and Setters
    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }
}
