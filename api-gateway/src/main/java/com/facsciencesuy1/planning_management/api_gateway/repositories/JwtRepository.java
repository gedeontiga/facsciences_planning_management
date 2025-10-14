package com.facsciencesuy1.planning_management.api_gateway.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Jwt;

@Repository
public interface JwtRepository extends MongoRepository<Jwt, String> {
    Optional<Jwt> findByToken(String token);

    void deleteAllByExpiredAtIsBefore(Instant instant);

    List<Jwt> findByUserId(String id);

    Optional<Jwt> findByUserIdAndExpiredAtIsAfter(String userId, Instant now);
}
