package com.facsciences_planning_management.facsciences_planning_management.managers.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.models.Validation;

@Repository
public interface ValidationRepository extends MongoRepository<Validation, String> {

    Optional<Validation> findByEmailAndExpiredAfter(String email, Instant now);

    Optional<Validation> findByEmail(String email);

    void deleteByExpiredBefore(Instant now);
}
