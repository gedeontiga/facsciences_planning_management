package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;

@Repository
public interface CourseRepository extends
        MongoRepository<Course, String>, CourseRepositoryCustom {
    Page<Course> findAllByObsoleteFalse(Pageable page);

    List<Course> findByObsoleteFalseAndTeacherId(String teacherId);

    Optional<Course> findByObsoleteFalseAndUeId(String ueId);

    boolean existsByUe(Ue ue);
}

interface CourseRepositoryCustom {
    List<Course> findByObsoleteFalseAndUeLevelId(String levelId);
}