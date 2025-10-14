package com.facsciencesuy1.planning_management.api_gateway.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Course;

@Repository
public interface CourseRepository extends
        MongoRepository<Course, String> {

    boolean existsByUeId(String ueId);

    boolean existsByUeIdAndTeacherId(String ueId, String teacherId);
}