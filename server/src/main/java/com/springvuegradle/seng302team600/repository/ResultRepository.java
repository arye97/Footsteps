package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByOutcome(Outcome outcome);

    boolean existsByOutcomeAndUserId(Outcome outcome, Long userId);
}
