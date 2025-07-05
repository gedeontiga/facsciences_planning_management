package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseRequest;

public interface CourseService {

    Page<CourseDTO> getAllCourses(Pageable page);

    CourseDTO createCourse(CourseRequest course);

    CourseDTO updateCourseTeacher(String courseId, String teacherId, String departmentId);

    CourseDTO updateCourseUe(String courseId, String ueId);

    CourseDTO updateCourseDuration(String courseId, Long duration);

    CourseDTO getCourse(String courseId);

    CourseDTO getCourseByUe(String ueId);

    List<CourseDTO> getCourseByLevel(String levelId);

    List<CourseDTO> getUnscheduledCourses(String levelId, String timetableId);

    void deleteCourse(String courseId);

}