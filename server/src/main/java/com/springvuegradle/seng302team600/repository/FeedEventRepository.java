package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.FeedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface FeedEventRepository extends JpaRepository<FeedEvent, Long> {

    @Query(value =
            "SELECT * " +
            "FROM feed_event AS FE LEFT JOIN feed_event_viewers AS FEV " +
            "ON FE.feed_event_id = FEV.feed_event_id " +
            "WHERE FEV.user_id = :userId " +
            "ORDER BY time_stamp DESC ", nativeQuery = true)
    Page<FeedEvent> findByViewerIdOrderByTimeStamp(@Param("userId") Long viewerId, Pageable pageable);
}
