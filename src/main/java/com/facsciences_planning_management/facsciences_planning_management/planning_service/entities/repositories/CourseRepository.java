package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;

@Repository
public interface CourseRepository extends
        MongoRepository<Course, String>, CourseRepositoryCustom {

    Page<Course> findAllByObsoleteFalse(Pageable page);

    List<Course> findByObsoleteFalseAndTeacherId(String teacherId);

    Optional<Course> findByObsoleteFalseAndUeId(String ueId);

    Optional<Course> findByUeIdAndObsoleteFalse(String ueId);

    boolean existsByUeId(String ueId);

    boolean existsByUeIdAndTeacherId(String ueId, String teacherId);
}

interface CourseRepositoryCustom {
    Page<Course> findByObsoleteFalseAndUeLevelId(String levelId, Pageable page);

    List<Course> findByObsoleteFalseAndUeLevelIdAndUeLevelSemester(String levelId, Semester semester);
}