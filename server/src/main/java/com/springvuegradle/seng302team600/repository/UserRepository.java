package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserId(Long id);

    User findByToken(String token);

    boolean existsUserByToken(String token);

    boolean existsUserByRole(int role);

    @Query(value="SELECT * FROM user WHERE user_id in ?1", nativeQuery=true)
    List<User> getUsersByIds(@Param("userIds") List<Long> userIds);
}
