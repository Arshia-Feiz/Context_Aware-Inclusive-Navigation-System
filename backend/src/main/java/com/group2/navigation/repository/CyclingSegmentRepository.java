package com.group2.navigation.repository;

import com.group2.navigation.model.CyclingSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for querying cycling network segments.
 * Used to identify bike-friendly paths and surface types.
 */
@Repository
public interface CyclingSegmentRepository extends JpaRepository<CyclingSegment, Long> {

    /**
     * Count cycling segments within a bounding box.
     * If count > 0, the area has cycling infrastructure nearby.
     */
    @Query("SELECT COUNT(c) FROM CyclingSegment c WHERE " +
           "c.latitude BETWEEN :minLat AND :maxLat AND " +
           "c.longitude BETWEEN :minLng AND :maxLng")
    long countInBoundingBox(@Param("minLat") double minLat,
                            @Param("maxLat") double maxLat,
                            @Param("minLng") double minLng,
                            @Param("maxLng") double maxLng);
}
