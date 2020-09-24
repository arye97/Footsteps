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

    @Query(value="SELECT AAT.activity_id FROM activity_activity_type AS AAT JOIN activity AS A ON (AAT.activity_id = A.activity_id) WHERE AAT.activity_type_id in :activityTypeIds AND A.fitness >= :minFitnessLevel AND A.fitness <= :maxFitnessLevel " +
            "GROUP BY AAT.activity_id HAVING COUNT(AAT.activity_id) = :numOfActivityTypes", nativeQuery=true)
    Page<Long> findByAllActivityTypeIds(@Param("activityTypeIds") List<Long> activityTypeIds,
                                        @Param("numOfActivityTypes") int numOfActivityTypes,
                                        Pageable pageable,
                                        @Param("minFitnessLevel") Integer minFitnessLevel,
                                        @Param("maxFitnessLevel") Integer maxFitnessLevel);

    @Query(value="SELECT AAT.activity_id FROM activity_activity_type AS AAT JOIN activity AS A ON (AAT.activity_id = A.activity_id) WHERE AAT.activity_type_id in :activityTypeIds AND A.fitness >= :minFitnessLevel AND A.fitness <= :maxFitnessLevel " +
            "GROUP BY AAT.activity_id", nativeQuery=true)
    Page<Long> findBySomeActivityTypeIds(@Param("activityTypeIds") List<Long> activityTypeIds,
                                         Pageable pageable,
                                         @Param("minFitnessLevel") Integer minFitnessLevel,
                                         @Param("maxFitnessLevel") Integer maxFitnessLevel);


}