package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Branch;

@Repository
public interface BranchRepository extends MongoRepository<Branch, String> {
    Optional<Branch> findByCode(String code);

    List<Branch> findByFacultyId(String facultyId);

    boolean existsByCode(String code);
}