package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Activity findByActivityId(Long id);

    // Finds both activities created by and participated by the given user
    @Query(value =
            "SELECT DISTINCT A.* " +
            "FROM activity AS A LEFT JOIN activity_participant AS AP " +
            "ON A.activity_id = AP.activity_id " +
            "WHERE A.creator_user_id = ?1 OR AP.user_id = ?1 " +
            "ORDER BY A.activity_id ASC", nativeQuery = true)
    List<Activity> findAllByUserId(Long userId);
}
