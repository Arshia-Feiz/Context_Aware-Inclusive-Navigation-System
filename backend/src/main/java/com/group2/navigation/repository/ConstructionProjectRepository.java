package com.group2.navigation.repository;

import com.group2.navigation.model.ConstructionProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for querying construction project locations.
 * Used to detect active construction zones along potential routes.
 */
@Repository
public interface ConstructionProjectRepository extends JpaRepository<ConstructionProject, Long> {

    /**
     * Find active construction projects within a bounding box.
     */
    @Query("SELECT c FROM ConstructionProject c WHERE " +
           "c.latitude BETWEEN :minLat AND :maxLat AND " +
           "c.longitude BETWEEN :minLng AND :maxLng AND " +
           "c.status = 'Active'")
    List<ConstructionProject> findActiveInBoundingBox(@Param("minLat") double minLat,
                                                      @Param("maxLat") double maxLat,
                                                      @Param("minLng") double minLng,
                                                      @Param("maxLng") double maxLng);

    /**
     * Count active construction projects within a bounding box.
     */
    @Query("SELECT COUNT(c) FROM ConstructionProject c WHERE " +
           "c.latitude BETWEEN :minLat AND :maxLat AND " +
           "c.longitude BETWEEN :minLng AND :maxLng AND " +
           "c.status = 'Active'")
    long countActiveInBoundingBox(@Param("minLat") double minLat,
                                  @Param("maxLat") double maxLat,
                                  @Param("minLng") double minLng,
                                  @Param("maxLng") double maxLng);
}
