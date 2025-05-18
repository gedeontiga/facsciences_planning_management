package com.facsciences_planning_management.facsciences_planning_management.planning_service.models.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Ue;

@Repository
public interface UeRepository extends MongoRepository<Ue, String> {
    List<Ue> findByLevelId(String levelId);
}