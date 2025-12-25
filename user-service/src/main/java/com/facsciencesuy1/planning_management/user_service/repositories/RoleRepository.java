package com.facsciencesuy1.planning_management.user_service.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Role;
import com.facsciencesuy1.planning_management.entities.types.RoleType;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByType(RoleType type);

    boolean existsByType(RoleType type);
}
