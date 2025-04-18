package com.facsciences_planning_management.facsciences_planning_management.managers.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.models.Users;
import com.facsciences_planning_management.facsciences_planning_management.models.Role;

@Repository
// @RepositoryRestResource
public interface UserRepository extends MongoRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    void deleteByEmail(String email);

    Optional<Users> findByEmailAndRole(String email, Role role);

    Optional<Users> findByEmailAndEnabledIsTrue(String email);
}
