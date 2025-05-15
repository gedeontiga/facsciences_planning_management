package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Jwt;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Users;

@Repository
public interface JwtRepository extends MongoRepository<Jwt, String> {
    Optional<Jwt> findByToken(String token);

    void deleteAllByExpiredAtIsBefore(Instant instant);

    Optional<Jwt> findByUserAndExpiredAtIsAfter(Users user, Instant now);
}
