package com.group2.navigation.repository;

import com.group2.navigation.model.HealthService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for querying health service (hospital/clinic) locations.
 * Used to find nearest hospital and nearby health services.
 */
@Repository
public interface HealthServiceRepository extends JpaRepository<HealthService, Long> {

    /**
     * Find health services within a bounding box.
     * Used to find nearby hospitals for distance calculation.
     */
    @Query("SELECT h FROM HealthService h WHERE " +
           "h.latitude BETWEEN :minLat AND :maxLat AND " +
           "h.longitude BETWEEN :minLng AND :maxLng")
    List<HealthService> findInBoundingBox(@Param("minLat") double minLat,
                                          @Param("maxLat") double maxLat,
                                          @Param("minLng") double minLng,
                                          @Param("maxLng") double maxLng);
}
