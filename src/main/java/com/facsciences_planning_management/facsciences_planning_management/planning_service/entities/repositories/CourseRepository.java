package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;

@Repository
public interface CourseRepository extends
        MongoRepository<Course, String> {
    List<Course> findByTeacherId(String teacherId);

    Optional<Course> findByUeId(String ueId);

    Optional<Course> findByUeCode(String ueCode);

    boolean existsByUe(Ue ue);
}
