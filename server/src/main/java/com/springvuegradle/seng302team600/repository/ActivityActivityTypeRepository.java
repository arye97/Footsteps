package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ActivityActivityTypeRepository extends JpaRepository<Activity, Long> {

    @Query(value =
            "SELECT activity_id " +
                    "FROM activity_activity_type " +
                    "WHERE activity_type_id in (:activityTypeIds) " +
                    "GROUP BY activity_id " +
                    "HAVING COUNT(DISTINCT activity_type_id) = :numOfActivityTypes", nativeQuery = true)
    List<Long> findByActivityTypeIds(
            @Param("activityTypeIds") List<Long> activityTypeIds,
            @Param("numOfActivityTypes") int numOfActivityTypes);
}