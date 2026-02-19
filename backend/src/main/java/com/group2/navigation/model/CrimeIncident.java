package com.group2.navigation.model;

import jakarta.persistence.*;

/**
 * Stores individual crime incidents from Toronto Police Open Data.
 * Used to calculate a crime score for any given location.
 */
@Entity
@Table(name = "crime_incidents", indexes = {
    @Index(name = "idx_crime_lat", columnList = "latitude"),
    @Index(name = "idx_crime_lng", columnList = "longitude"),
    @Index(name = "idx_crime_lat_lng", columnList = "latitude, longitude")
})
public class CrimeIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_unique_id")
    private String eventUniqueId;

    @Column(name = "occ_year")
    private int occYear;

    @Column(name = "offence")
    private String offence;

    @Column(name = "category")
    private String category;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "hood_id")
    private String hoodId;

    @Column(name = "neighbourhood")
    private String neighbourhood;

    public CrimeIncident() {}

    public CrimeIncident(String eventUniqueId, int occYear, String offence,
                         String category, double latitude, double longitude,
                         String hoodId, String neighbourhood) {
        this.eventUniqueId = eventUniqueId;
        this.occYear = occYear;
        this.offence = offence;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hoodId = hoodId;
        this.neighbourhood = neighbourhood;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventUniqueId() { return eventUniqueId; }
    public void setEventUniqueId(String eventUniqueId) { this.eventUniqueId = eventUniqueId; }

    public int getOccYear() { return occYear; }
    public void setOccYear(int occYear) { this.occYear = occYear; }

    public String getOffence() { return offence; }
    public void setOffence(String offence) { this.offence = offence; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getHoodId() { return hoodId; }
    public void setHoodId(String hoodId) { this.hoodId = hoodId; }

    public String getNeighbourhood() { return neighbourhood; }
    public void setNeighbourhood(String neighbourhood) { this.neighbourhood = neighbourhood; }
}
