package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.IsFollowingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ActivityParticipantRepository extends JpaRepository<Activity, Long> {

    @Query(value =
            "SELECT activity_id " +
            "FROM activity_participant " +
            "WHERE user_id = :userId " +
            "GROUP BY activity_id", nativeQuery = true)
    List<Long> findActivitiesByParticipantId(@Param("userId") Long userId);


    @Query(value =
            "SELECT user_id " +
            "FROM activity_participant " +
            "WHERE activity_id = :activityId " +
            "GROUP BY user_id", nativeQuery = true)
    List<Long> findUsersByActivityId(@Param("activityId") Long activityId);


    @Query(value =
            "SELECT COUNT(*) " +
            "FROM activity_participant " +
            "WHERE user_id = :userId " +
            "AND activity_id = :activityId", nativeQuery = true)
    Long existsByActivityIdAndUserId(@Param("activityId") long activityId, @Param("userId") long userId);
}
