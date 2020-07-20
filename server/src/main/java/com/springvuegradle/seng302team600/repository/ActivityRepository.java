package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Activity findByActivityId(Long id);

    //If you see that this is highlighted red, it is NOT AN ERROR, please don't delete.
    @Query(value = "SELECT * FROM activity WHERE creator_user_id = ?1 ORDER BY activity_id ASC", nativeQuery = true)
    List<Activity> findAllByUserId(Long userId);
}