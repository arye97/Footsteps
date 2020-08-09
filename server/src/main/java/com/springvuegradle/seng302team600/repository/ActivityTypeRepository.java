package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {

    List<ActivityType> findAll();
    boolean existsActivityTypeByName(String name);
    ActivityType findActivityTypeByName(String name);

    @Query(value="SELECT * FROM activity_type WHERE name IN ?1", nativeQuery=true)
    List<Long> findActivityTypeIdsByNames(@Param("activity_types") List<String> activityTypes);
}