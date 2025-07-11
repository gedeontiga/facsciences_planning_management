package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Level;

@Repository
public interface LevelRepository extends MongoRepository<Level, String> {
    Optional<Level> findByCode(String code);

    List<Level> findByBranchId(String branchId);

    boolean existsByCode(String code);
}