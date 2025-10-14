package com.facsciencesuy1.planning_management.planning_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Level;

@Repository
public interface LevelRepository extends MongoRepository<Level, String> {
    Optional<Level> findByCode(String code);

    List<Level> findByBranchId(String branchId);

    boolean existsByCode(String code);
}