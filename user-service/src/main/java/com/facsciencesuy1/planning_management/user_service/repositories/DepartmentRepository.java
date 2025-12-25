package com.facsciencesuy1.planning_management.user_service.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Department;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);
}