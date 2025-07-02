package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Department;
import java.util.List;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String>, DepartmentRepositoryCustom {
    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);
}

interface DepartmentRepositoryCustom {
    List<Department> findByBranchFacultyId(String facultyId);
}