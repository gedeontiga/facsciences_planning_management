package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseDTO;

public interface CourseService {

    List<CourseDTO> getAllCourses();

    CourseDTO createCourse(String teacherId, String ueId, Long duration);

    CourseDTO updateCourseTeacher(String courseId, String teacherId);

    CourseDTO updateCourseUe(String courseId, String ueId);

    CourseDTO updateCourseDuration(String courseId, Long duration);

    CourseDTO getCourse(String courseId);

    CourseDTO getCourseByUeId(String ueId);

    CourseDTO getCourseByUeCode(String ueCode);

    void deleteCourse(String courseId);

}