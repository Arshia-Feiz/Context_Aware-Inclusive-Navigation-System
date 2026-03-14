package com.group2.navigation.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * User preferences for route calculation.
 *
 * Each weight is 0-10 where 0 = "don't care" and 10 = "maximum importance".
 * The A* edge-cost formula scales penalties proportionally to these weights.
 */
public class UserPreferences {

    @DecimalMin(value = "0", message = "Wheelchair weight must be at least 0")
    @DecimalMax(value = "10", message = "Wheelchair weight must be at most 10")
    private double wheelchairWeight;

    @DecimalMin(value = "0", message = "Crime weight must be at least 0")
    @DecimalMax(value = "10", message = "Crime weight must be at most 10")
    private double crimeWeight;

    @DecimalMin(value = "0", message = "Lighting weight must be at least 0")
    @DecimalMax(value = "10", message = "Lighting weight must be at most 10")
    private double lightingWeight;

    @DecimalMin(value = "0", message = "Construction weight must be at least 0")
    @DecimalMax(value = "10", message = "Construction weight must be at most 10")
    private double constructionWeight;

    @Min(value = 0, message = "Time of day must be at least 0")
    @Max(value = 23, message = "Time of day must be at most 23")
    private int timeOfDay;

    @DecimalMin(value = "0", message = "Max distance to hospital must be at least 0")
    @DecimalMax(value = "50000", message = "Max distance to hospital must be at most 50000 meters")
    private double maxDistanceToHospital;

    public UserPreferences() {
        this.wheelchairWeight = 0;
        this.crimeWeight = 0;
        this.lightingWeight = 0;
        this.constructionWeight = 0;
        this.timeOfDay = 12;
        this.maxDistanceToHospital = 0;
    }

    public double getWheelchairWeight() {
        return wheelchairWeight;
    }

    public void setWheelchairWeight(double wheelchairWeight) {
        this.wheelchairWeight = wheelchairWeight;
    }

    public double getCrimeWeight() {
        return crimeWeight;
    }

    public void setCrimeWeight(double crimeWeight) {
        this.crimeWeight = crimeWeight;
    }

    public double getLightingWeight() {
        return lightingWeight;
    }

    public void setLightingWeight(double lightingWeight) {
        this.lightingWeight = lightingWeight;
    }

    public double getConstructionWeight() {
        return constructionWeight;
    }

    public void setConstructionWeight(double constructionWeight) {
        this.constructionWeight = constructionWeight;
    }

    public int getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(int timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public double getMaxDistanceToHospital() {
        return maxDistanceToHospital;
    }

    public void setMaxDistanceToHospital(double maxDistanceToHospital) {
        this.maxDistanceToHospital = maxDistanceToHospital;
    }
}
