package com.facsciencesuy1.planning_management.api_gateway.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Ue;

@Repository
public interface UeRepository extends MongoRepository<Ue, String> {

    boolean existsByCode(String code);
}