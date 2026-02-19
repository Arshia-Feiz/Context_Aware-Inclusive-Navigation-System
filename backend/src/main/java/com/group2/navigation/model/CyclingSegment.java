package com.group2.navigation.model;

import jakarta.persistence.*;

/**
 * Stores cycling network segments from Toronto Open Data.
 * Used to identify bike-friendly routes and surface types.
 */
@Entity
@Table(name = "cycling_segments", indexes = {
    @Index(name = "idx_cycle_lat", columnList = "latitude"),
    @Index(name = "idx_cycle_lng", columnList = "longitude"),
    @Index(name = "idx_cycle_lat_lng", columnList = "latitude, longitude")
})
public class CyclingSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "infra_type")
    private String infraType;

    @Column(name = "surface")
    private String surface;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    public CyclingSegment() {}

    public CyclingSegment(String streetName, String infraType, String surface,
                          double latitude, double longitude) {
        this.streetName = streetName;
        this.infraType = infraType;
        this.surface = surface;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }

    public String getInfraType() { return infraType; }
    public void setInfraType(String infraType) { this.infraType = infraType; }

    public String getSurface() { return surface; }
    public void setSurface(String surface) { this.surface = surface; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
