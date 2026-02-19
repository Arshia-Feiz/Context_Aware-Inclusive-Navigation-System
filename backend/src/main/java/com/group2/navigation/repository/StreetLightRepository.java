package com.group2.navigation.repository;

import com.group2.navigation.model.StreetLight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for querying street light locations.
 * Used to determine if a street segment has lighting.
 */
@Repository
public interface StreetLightRepository extends JpaRepository<StreetLight, Long> {

    /**
     * Check if any street lights exist within a bounding box.
     * A small bounding box (~30-50m) simulates "is this spot lit?"
     */
    @Query("SELECT COUNT(s) FROM StreetLight s WHERE " +
           "s.latitude BETWEEN :minLat AND :maxLat AND " +
           "s.longitude BETWEEN :minLng AND :maxLng")
    long countInBoundingBox(@Param("minLat") double minLat,
                            @Param("maxLat") double maxLat,
                            @Param("minLng") double minLng,
                            @Param("maxLng") double maxLng);
}
