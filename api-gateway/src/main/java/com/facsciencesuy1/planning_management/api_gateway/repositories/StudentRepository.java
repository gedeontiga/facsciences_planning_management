package com.facsciencesuy1.planning_management.api_gateway.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Student;

import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findByEmailAndEnabledIsTrue(String email);
}
