package com.group2.navigation.model;

import jakarta.persistence.*;

/**
 * Stores street light pole locations from Toronto Open Data.
 * Used to determine if a street segment is lit or not.
 */
@Entity
@Table(name = "street_lights", indexes = {
    @Index(name = "idx_light_lat", columnList = "latitude"),
    @Index(name = "idx_light_lng", columnList = "longitude"),
    @Index(name = "idx_light_lat_lng", columnList = "latitude, longitude")
})
public class StreetLight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    public StreetLight() {}

    public StreetLight(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
