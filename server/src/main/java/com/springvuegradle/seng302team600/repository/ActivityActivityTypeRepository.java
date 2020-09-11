package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ActivityActivityTypeRepository extends JpaRepository<Activity, Long> {

    @Query(value="SELECT activity_id FROM activity_activity_type WHERE activity_type_id in ?1 " +
            "GROUP BY activity_id HAVING COUNT(DISTINCT activity_id) = ?2", nativeQuery=true)
    Page<Long> findByAllActivityTypeIds(@Param("activityTypeIds") List<Long> activityTypeIds,
                                        @Param("numOfActivityTypes") int numOfActivityTypes,
                                        Pageable pageable);

    @Query(value="SELECT activity_id FROM activity_activity_type WHERE activity_type_id in ?1 " +
            "GROUP BY activity_id", nativeQuery=true)
    Page<Long> findBySomeActivityTypeIds(@Param("activityTypeIds") List<Long> activityTypeIds,
                                        Pageable pageable);
}