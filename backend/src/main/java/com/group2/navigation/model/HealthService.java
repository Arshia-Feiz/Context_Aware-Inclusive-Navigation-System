package com.group2.navigation.model;

import jakarta.persistence.*;

/**
 * Stores health service locations (hospitals, clinics, etc.) from Toronto Open Data.
 * Used to calculate distance to nearest hospital for route preferences.
 */
@Entity
@Table(name = "health_services", indexes = {
    @Index(name = "idx_health_lat", columnList = "latitude"),
    @Index(name = "idx_health_lng", columnList = "longitude"),
    @Index(name = "idx_health_lat_lng", columnList = "latitude, longitude")
})
public class HealthService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agency_name")
    private String agencyName;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "accessibility", length = 1000)
    private String accessibility;

    @Column(name = "phone")
    private String phone;

    public HealthService() {}

    public HealthService(String agencyName, String address, double latitude,
                         double longitude, String accessibility, String phone) {
        this.agencyName = agencyName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accessibility = accessibility;
        this.phone = phone;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getAccessibility() { return accessibility; }
    public void setAccessibility(String accessibility) { this.accessibility = accessibility; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
