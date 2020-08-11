package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Outcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource
public interface OutcomeRepository extends JpaRepository<Outcome, Long> {
    
    List<Outcome> findByActivityId(Long activityId);
}
