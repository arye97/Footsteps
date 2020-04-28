package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface EmailRepository extends JpaRepository<Email, Long> {

    Email findByEmail(String email);

    boolean existsEmailByEmail(String email);
}
