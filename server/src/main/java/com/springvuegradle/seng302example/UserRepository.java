package com.springvuegradle.seng302example;

import com.springvuegradle.seng302example.model.Emails;
import com.springvuegradle.seng302example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);


    User findByEmails(Emails emails);


    User findByFirstName(String name);
}
