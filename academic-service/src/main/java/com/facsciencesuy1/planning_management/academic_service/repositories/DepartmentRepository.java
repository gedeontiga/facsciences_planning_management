package com.facsciencesuy1.planning_management.academic_service.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Department;

import java.util.List;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String>, DepartmentRepositoryCustom {
    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);
}

interface DepartmentRepositoryCustom {
    List<Department> findByBranchFacultyId(String facultyId);
}