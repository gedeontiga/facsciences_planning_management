package com.facsciences_planning_management.facsciences_planning_management.entities.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.AcademicYear;
import java.util.Optional;

@Repository
public interface AcademicYearRepository extends MongoRepository<AcademicYear, String> {
    boolean existsByLabel(String label);

    Optional<AcademicYear> findByLabel(String label);
}
