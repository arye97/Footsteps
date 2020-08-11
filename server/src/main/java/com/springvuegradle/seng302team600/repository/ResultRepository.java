package com.springvuegradle.seng302team600.repository;


import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Result;
import com.springvuegradle.seng302team600.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ResultRepository extends JpaRepository<Result, Long> {

    @Query(value = "SELECT * FROM result WHERE outcome_id = ?1", nativeQuery=true)
    List<Result> findAllResultsByOutcomeId(@Param("outcome_id") Long outcome_id);
}
