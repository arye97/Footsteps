package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {

}