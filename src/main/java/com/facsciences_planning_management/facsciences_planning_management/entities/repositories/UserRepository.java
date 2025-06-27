package com.facsciences_planning_management.facsciences_planning_management.entities.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.types.RoleType;

@Repository
public interface UserRepository extends MongoRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    Optional<Users> findByEmailAndRoleId(String email, String roleId);

    Optional<Users> findByEmailAndEnabledIsTrue(String email);

    Page<Users> findByRoleId(String roleId, Pageable page);

    Page<Users> findByRoleType(RoleType type, Pageable page);
}
