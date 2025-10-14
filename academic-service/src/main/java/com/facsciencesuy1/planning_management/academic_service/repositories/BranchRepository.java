package com.facsciencesuy1.planning_management.academic_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Branch;

@Repository
public interface BranchRepository extends MongoRepository<Branch, String> {
    Optional<Branch> findByCode(String code);

    List<Branch> findByFacultyId(String facultyId);

    boolean existsByCode(String code);
}