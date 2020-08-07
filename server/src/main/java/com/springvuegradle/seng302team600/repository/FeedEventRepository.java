package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.FeedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface FeedEventRepository extends JpaRepository<FeedEvent, Long> {

    List<FeedEvent> findByUserIdOrderByTimeStamp(Long userId);
}
