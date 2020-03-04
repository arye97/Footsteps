package com.springvuegradle.seng302example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
interface StudentRepository extends JpaRepository<Student, Long> {}
