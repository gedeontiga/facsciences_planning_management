package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.UeRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.CourseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UeRepository ueRepository;
    private final UserRepository userRepository;

    @Override
    public CourseDTO createCourse(String teacherId, String ueId, Long duration) {
        Users teacher = userRepository.findById(teacherId).orElseThrow(() -> new RuntimeException("User not found"));
        Ue ue = ueRepository.findById(ueId).orElseThrow(() -> new RuntimeException("Ue not found"));
        return courseRepository.save(
                Course.builder()
                        .teacher(teacher)
                        .ue(ue)
                        .duration(Duration.ofHours(duration))
                        .build())
                .toDTO();
    }

    @Override
    public CourseDTO updateCourseTeacher(String courseId, String teacherId) {
        Users teacher = userRepository.findById(teacherId).orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        course.setTeacher(teacher);
        return courseRepository.save(course).toDTO();
    }

    @Override
    public CourseDTO updateCourseUe(String courseId, String ueId) {
        Ue ue = ueRepository.findById(ueId).orElseThrow(() -> new RuntimeException("Ue not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        course.setUe(ue);
        return courseRepository.save(course).toDTO();
    }

    @Override
    public CourseDTO updateCourseDuration(String courseId, Long duration) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        course.setDuration(Duration.ofHours(duration));
        return courseRepository.save(course).toDTO();
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream().map(Course::toDTO).collect(Collectors.toList());
    }

    @Override
    public CourseDTO getCourse(String courseId) {
        return courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found")).toDTO();
    }

    @Override
    public CourseDTO getCourseByUeId(String ueId) {
        return courseRepository.findByUeId(ueId).orElseThrow(() -> new RuntimeException("Course not found")).toDTO();
    }

    @Override
    public CourseDTO getCourseByUeCode(String ueCode) {
        return courseRepository.findByUeCode(ueCode).orElseThrow(() -> new RuntimeException("Course not found"))
                .toDTO();
    }

    @Override
    public void deleteCourse(String courseId) {
        courseRepository.deleteById(courseId);
    }
}
