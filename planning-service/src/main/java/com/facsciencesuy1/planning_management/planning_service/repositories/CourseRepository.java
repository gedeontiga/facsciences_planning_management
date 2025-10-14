package com.facsciencesuy1.planning_management.planning_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Course;
import com.facsciencesuy1.planning_management.entities.types.Semester;

@Repository
public interface CourseRepository extends
        MongoRepository<Course, String> {

    Page<Course> findAllByObsoleteFalse(Pageable page);

    List<Course> findByObsoleteFalseAndTeacherId(String teacherId);

    Optional<Course> findByObsoleteFalseAndUeId(String ueId);

    Optional<Course> findByUeIdAndObsoleteFalse(String ueId);

    boolean existsByUeId(String ueId);

    boolean existsByUeIdAndTeacherId(String ueId, String teacherId);

    Page<Course> findByObsoleteFalseAndLevelId(String levelId, Pageable page);

    List<Course> findByObsoleteFalseAndLevelIdAndSemester(String levelId, Semester semester);
}

// interface CourseRepositoryCustom {

// }