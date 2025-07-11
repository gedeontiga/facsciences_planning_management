package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Jwt;

@Repository
public interface JwtRepository extends MongoRepository<Jwt, String> {
    Optional<Jwt> findByToken(String token);

    void deleteAllByExpiredAtIsBefore(Instant instant);

    List<Jwt> findByUserId(String id);

    Optional<Jwt> findByUserIdAndExpiredAtIsAfter(String userId, Instant now);
}
