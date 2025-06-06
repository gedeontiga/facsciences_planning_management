package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Validation;

@Repository
public interface ValidationRepository extends MongoRepository<Validation, String> {

    Optional<Validation> findByActivationTokenAndExpiredIsAfter(String email, Instant now);

    void deleteByExpiredBefore(Instant now);
}
