package com.facsciencesuy1.planning_management.api_gateway.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Validation;

@Repository
public interface ValidationRepository extends MongoRepository<Validation, String> {

    Optional<Validation> findByActivationTokenAndExpiredIsAfter(String email, Instant now);

    void deleteByExpiredBefore(Instant now);
}
