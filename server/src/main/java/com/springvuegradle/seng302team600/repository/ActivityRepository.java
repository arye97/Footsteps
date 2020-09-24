package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, Long>, ActivityRepositoryCustom {

    Activity findByActivityId(Long id);

    // Finds both activities created by and participated by the given user
    @Query(value =
            "SELECT DISTINCT A.* " +
            "FROM activity AS A LEFT JOIN activity_participant AS AP " +
            "ON A.activity_id = AP.activity_id " +
            "WHERE creator_user_id = :userId OR user_id = :userId " +
            "ORDER BY A.activity_id ASC", nativeQuery = true)
    List<Activity> findAllByUserId(@Param("userId") Long userId);

    @Query(value =
            "SELECT DISTINCT A.* " +
            "FROM activity AS A LEFT JOIN activity_participant AS AP " +
            "ON A.activity_id = AP.activity_id " +
            "WHERE creator_user_id = :userId OR user_id = :userId " +
            "ORDER BY A.activity_id ASC", nativeQuery = true)
    Slice<Activity> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value =
            "SELECT COUNT(DISTINCT A.activity_id) " +
            "FROM activity AS A LEFT JOIN activity_participant AS AP " +
            "ON A.activity_id = AP.activity_id " +
            "WHERE creator_user_id = :userId OR user_id = :userId " +
            "ORDER BY A.activity_id ASC", nativeQuery = true)
    Integer countAllByUserId(@Param("userId") Long userId);

    @Query(value =
            "SELECT DISTINCT A.* " +
            "FROM activity AS A LEFT JOIN activity_participant AS AP " +
            "ON A.activity_id = AP.activity_id " +
            "WHERE is_continuous = true AND (creator_user_id = :userId OR user_id = :userId) " +
            "ORDER BY A.activity_id ASC", nativeQuery = true)
    List<Activity> findAllContinuousByUserId(@Param("userId") Long userId);

    @Query(value =
            "SELECT DISTINCT A.* " +
            "FROM activity AS A LEFT JOIN activity_participant AS AP " +
            "ON A.activity_id = AP.activity_id " +
            "WHERE is_continuous = false AND (creator_user_id = :userId OR user_id = :userId) " +
            "ORDER BY A.activity_id ASC", nativeQuery = true)
    List<Activity> findAllDurationByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM activity WHERE activity_id in ?1", nativeQuery = true)
    List<Activity> getActivitiesByIds(@Param("userIds") List<Long> activityIds);

    @Query(value =
            "SELECT * FROM activity WHERE activity_name LIKE ?1", nativeQuery = true)
    List<Activity> findAllByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT a.*" +
            "FROM activity as a " +
            "INNER JOIN location as l " +
            "ON (a.location_location_id = l.location_id) " +
            "WHERE " +
            "   111.111 * " +
            "    DEGREES(ACOS(LEAST(1.0, COS(RADIANS(:userLatitude)) " +
            "         * COS(RADIANS(l.Latitude)) " +
            "         * COS(RADIANS(:userLongitude - l.Longitude)) " +
            "         + SIN(RADIANS(:userLatitude)) " +
            "         * SIN(RADIANS(l.Latitude))))) <= :maxDistance " +
            "GROUP BY activity_id " +
            "ORDER BY 111.111 * " +
            "    DEGREES(ACOS(LEAST(1.0, COS(RADIANS(:userLatitude)) " +
            "         * COS(RADIANS(l.Latitude)) " +
            "         * COS(RADIANS(:userLongitude - l.Longitude)) " +
            "         + SIN(RADIANS(:userLatitude)) " +
            "         * SIN(RADIANS(l.Latitude)))))", nativeQuery = true)
    Slice<Activity> findAllWithinDistance(@Param("userLatitude") Double userLatitude,
                                          @Param("userLongitude") Double userLongitude,
                                          @Param("maxDistance") Double maxDistance,
                                          Pageable pageable);

    @Query(value = "SELECT a.*" +
            "FROM activity as a " +
            "INNER JOIN location as l " +
            "ON (a.location_location_id = l.location_id) " +
            "INNER JOIN activity_activity_type as act " +
            "ON (act.activity_type_id IN :activityTypeIds AND act.activity_id = a.activity_id) " +
            "WHERE " +
            "   111.111 * " +
            "    DEGREES(ACOS(LEAST(1.0, COS(RADIANS(:userLatitude)) " +
            "         * COS(RADIANS(l.Latitude)) " +
            "         * COS(RADIANS(:userLongitude - l.Longitude)) " +
            "         + SIN(RADIANS(:userLatitude)) " +
            "         * SIN(RADIANS(l.Latitude))))) <= :maxDistance " +
            "   AND a.fitness >= :minFitnessLevel " +
            "   AND a.fitness <= :maxFitnessLevel " +
            "GROUP BY a.activity_id " +
            "ORDER BY 111.111 * " +
            "    DEGREES(ACOS(LEAST(1.0, COS(RADIANS(:userLatitude)) " +
            "         * COS(RADIANS(l.Latitude)) " +
            "         * COS(RADIANS(:userLongitude - l.Longitude)) " +
            "         + SIN(RADIANS(:userLatitude)) " +
            "         * SIN(RADIANS(l.Latitude)))))", nativeQuery = true)
    Slice<Activity> findAllWithinDistanceBySomeActivityTypeIds(@Param("userLatitude") Double userLatitude,
                                                               @Param("userLongitude") Double userLongitude,
                                                               @Param("maxDistance") Double maxDistance,
                                                               @Param("activityTypeIds") List<Long> activityTypeIds,
                                                               @Param("minFitnessLevel") Integer minFitnessLevel,
                                                               @Param("maxFitnessLevel") Integer maxFitnessLevel,
                                                               Pageable pageable);

    @Query(value = "SELECT a.*" +
            "FROM activity as a " +
            "INNER JOIN location as l " +
            "ON (a.location_location_id = l.location_id) " +
            "INNER JOIN activity_activity_type as act " +
            "ON (act.activity_type_id IN :activityTypeIds AND act.activity_id = a.activity_id) " +
            "WHERE " +
            "   111.111 * " +
            "    DEGREES(ACOS(LEAST(1.0, COS(RADIANS(:userLatitude)) " +
            "         * COS(RADIANS(l.Latitude)) " +
            "         * COS(RADIANS(:userLongitude - l.Longitude)) " +
            "         + SIN(RADIANS(:userLatitude)) " +
            "         * SIN(RADIANS(l.Latitude))))) <= :maxDistance " +
            "   AND a.fitness >= :minFitnessLevel " +
            "   AND a.fitness <= :maxFitnessLevel " +
            "GROUP BY a.activity_id HAVING COUNT(a.activity_type_id) = :numActivityTypes " +
            "ORDER BY 111.111 * " +
            "    DEGREES(ACOS(LEAST(1.0, COS(RADIANS(:userLatitude)) " +
            "         * COS(RADIANS(l.Latitude)) " +
            "         * COS(RADIANS(:userLongitude - l.Longitude)) " +
            "         + SIN(RADIANS(:userLatitude)) " +
            "         * SIN(RADIANS(l.Latitude)))))", nativeQuery = true)
    Slice<Activity> findAllWithinDistanceByAllActivityTypeIds(@Param("userLatitude") Double userLatitude,
                                                              @Param("userLongitude") Double userLongitude,
                                                              @Param("maxDistance") Double maxDistance,
                                                              @Param("activityTypeIds") List<Long> activityTypeIds,
                                                              @Param("numActivityTypes") int numActivityTypes,
                                                              @Param("minFitnessLevel") Integer minFitnessLevel,
                                                              @Param("maxFitnessLevel") Integer maxFitnessLevel,
                                                              Pageable pageable);

    @Query(value = "SELECT COUNT(*) " +
            "FROM activity as a " +
            "INNER JOIN location as l " +
            "ON (a.location_location_id = l.location_id) " +
            "WHERE " +
            "   111.111 * " +
            "    DEGREES(ACOS(LEAST(1.0, COS(RADIANS(:userLatitude)) " +
            "         * COS(RADIANS(l.Latitude)) " +
            "         * COS(RADIANS(:userLongitude - l.Longitude)) " +
            "         + SIN(RADIANS(:userLatitude)) " +
            "         * SIN(RADIANS(l.Latitude))))) <= :maxDistance", nativeQuery = true)
    int countAllWithinDistance(@Param("userLatitude") Double userLatitude,
                               @Param("userLongitude") Double userLongitude,
                               @Param("maxDistance") Double maxDistance);

    @Query(value = "SELECT SUM(count) FROM " +
            "(SELECT COUNT(*) as count " +
            "FROM activity as a " +
            "INNER JOIN location as l " +
            "ON (a.location_location_id = l.location_id) " +
            "INNER JOIN activity_activity_type as act " +
            "ON (act.activity_type_id IN :activityTypeIds AND act.activity_id = a.activity_id) " +
            "WHERE " +
            "   111.111 * " +
            "    DEGREES(ACOS(LEAST(1.0, COS(RADIANS(:userLatitude)) " +
            "         * COS(RADIANS(l.Latitude)) " +
            "         * COS(RADIANS(:userLongitude - l.Longitude)) " +
            "         + SIN(RADIANS(:userLatitude)) " +
            "         * SIN(RADIANS(l.Latitude))))) <= :maxDistance " +
            "GROUP BY a.activity_id) as counts", nativeQuery = true)
    int countAllWithinDistanceBySomeActivityTypeIds(@Param("userLatitude") Double userLatitude,
                                                                @Param("userLongitude") Double userLongitude,
                                                                @Param("maxDistance") Double maxDistance,
                                                                @Param("activityTypeIds") List<Long> activityTypeIds);

    @Query(value = "SELECT SUM(count) FROM " +
            "(SELECT COUNT(*) as count " +
            "FROM activity as a " +
            "INNER JOIN location as l " +
            "ON (a.location_location_id = l.location_id) " +
            "INNER JOIN activity_activity_type as act " +
            "ON (act.activity_type_id IN :activityTypeIds AND act.activity_id = a.activity_id) " +
            "WHERE " +
            "   111.111 * " +
            "    DEGREES(ACOS(LEAST(1.0, COS(RADIANS(:userLatitude)) " +
            "         * COS(RADIANS(l.Latitude)) " +
            "         * COS(RADIANS(:userLongitude - l.Longitude)) " +
            "         + SIN(RADIANS(:userLatitude)) " +
            "         * SIN(RADIANS(l.Latitude))))) <= :maxDistance " +
            "GROUP BY a.activity_id HAVING COUNT(a.activity_id) = :numActivityTypes) as counts", nativeQuery = true)
    int countAllWithinDistanceByAllActivityTypeIds(@Param("userLatitude") Double userLatitude,
                                                               @Param("userLongitude") Double userLongitude,
                                                               @Param("maxDistance") Double maxDistance,
                                                               @Param("activityTypeIds") List<Long> activityTypeIds,
                                                               @Param("numActivityTypes") int numActivityTypes);
}
