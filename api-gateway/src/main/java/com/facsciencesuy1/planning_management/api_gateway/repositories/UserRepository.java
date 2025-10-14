package com.facsciencesuy1.planning_management.api_gateway.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Users;
import com.facsciencesuy1.planning_management.entities.types.RoleType;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    Optional<Users> findByEmailAndRoleId(String email, String roleId);

    Optional<Users> findByEmailAndEnabledIsTrue(String email);

    Page<Users> findByRoleId(String roleId, Pageable page);

    Page<Users> findByRoleType(RoleType type, Pageable page);

    List<Users> findByRoleTypeAndEnabledTrue(RoleType roleType);

    List<Users> findByRoleTypeInAndEnabledTrue(List<RoleType> roleType);
}
