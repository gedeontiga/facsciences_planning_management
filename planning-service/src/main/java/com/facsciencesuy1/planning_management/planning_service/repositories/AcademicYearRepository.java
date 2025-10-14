package com.facsciencesuy1.planning_management.planning_service.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.AcademicYear;

import java.util.Optional;

@Repository
public interface AcademicYearRepository extends MongoRepository<AcademicYear, String> {
    boolean existsByLabel(String label);

    Optional<AcademicYear> findByLabel(String label);
}
