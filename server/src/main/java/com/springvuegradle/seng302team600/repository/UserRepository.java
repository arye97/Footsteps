package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserId(Long id);

    User findByToken(String token);

    boolean existsUserByToken(String token);

    boolean existsUserByRole(int role);
}
