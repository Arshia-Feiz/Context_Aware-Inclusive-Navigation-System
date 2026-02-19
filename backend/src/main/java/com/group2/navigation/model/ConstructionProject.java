package com.group2.navigation.model;

import jakarta.persistence.*;

/**
 * Stores road construction/reconstruction projects from Toronto Open Data.
 * Used to penalize or block routes through construction zones.
 */
@Entity
@Table(name = "construction_projects", indexes = {
    @Index(name = "idx_const_lat", columnList = "latitude"),
    @Index(name = "idx_const_lng", columnList = "longitude"),
    @Index(name = "idx_const_lat_lng", columnList = "latitude, longitude")
})
public class ConstructionProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_type")
    private String projectType;

    @Column(name = "location", length = 1000)
    private String location;

    @Column(name = "status")
    private String status;

    @Column(name = "duration")
    private String duration;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    public ConstructionProject() {}

    public ConstructionProject(String projectType, String location, String status,
                               String duration, double latitude, double longitude) {
        this.projectType = projectType;
        this.location = location;
        this.status = status;
        this.duration = duration;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
