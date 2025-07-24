package com.facsciences_planning_management.facsciences_planning_management.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.entities.Teacher;
import java.util.Optional;

@Repository
public interface TeacherRepository extends MongoRepository<Teacher, String> {
    Page<Teacher> findByDepartmentId(String departmentId, Pageable page);

    Optional<Teacher> findByEmailAndEnabledIsTrue(String email);

    Optional<Teacher> findByEmail(String email);
}
