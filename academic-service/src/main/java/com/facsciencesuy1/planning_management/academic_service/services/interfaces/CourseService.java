package com.facsciencesuy1.planning_management.academic_service.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciencesuy1.planning_management.academic_service.utils.dtos.CourseRequest;
import com.facsciencesuy1.planning_management.dtos.CourseDTO;

public interface CourseService {

    Page<CourseDTO> getAllCourses(Pageable page);

    CourseDTO createCourse(CourseRequest course);

    CourseDTO updateCourseTeacher(String courseId, String teacherId, String departmentId);

    CourseDTO updateCourseUe(String courseId, String ueId);

    CourseDTO updateCourseDuration(String courseId, Long duration);

    CourseDTO getCourse(String courseId);

    CourseDTO getCourseByUe(String ueId);

    Page<CourseDTO> getCourseByLevel(String levelId, Pageable page);

    List<CourseDTO> getUnscheduledCourses(String levelId, String timetableId);

    // void deleteCourse(String courseId);

}