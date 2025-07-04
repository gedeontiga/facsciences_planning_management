package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;

@Repository
public interface UeRepository extends MongoRepository<Ue, String> {
    Page<Ue> findByLevelId(String levelId, Pageable page);

    Optional<Ue> findByCode(String code);

    boolean existsByCode(String code);

    List<Ue> findByCreditsAndLevelId(Integer credits, String levelId);

    List<Ue> findByCategoryAndLevelId(String category, String levelId);

    Page<Ue> findByLevelIdAndAssignedFalse(String levelId, Pageable page);

    Page<Ue> findByAssignedFalse(Pageable page);
}