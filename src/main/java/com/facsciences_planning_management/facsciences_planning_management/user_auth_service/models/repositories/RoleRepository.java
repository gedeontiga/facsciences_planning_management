package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Role;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.types.RoleType;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByType(RoleType type);
}
