package com.facsciences_planning_management.facsciences_planning_management.planning_service.models.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Level;

@Repository
public interface LevelRepository extends MongoRepository<Level, String> {
}
