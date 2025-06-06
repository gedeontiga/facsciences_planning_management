package com.facsciences_planning_management.facsciences_planning_management.entities.repositories;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.types.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Role;

@Repository
public interface UserRepository extends MongoRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    Optional<Users> findByEmailAndRole(String email, Role role);

    Optional<Users> findByEmailAndEnabledIsTrue(String email);

    Set<Users> findByRoleType(RoleType roleType);
}
