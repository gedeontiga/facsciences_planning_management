package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.CourseService;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/ue/{ueId}")
    public ResponseEntity<CourseDTO> getCourseByUeId(@PathVariable String ueId) {
        CourseDTO course = courseService.getCourseByUeId(ueId);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/ue/code/{ueCode}")
    public ResponseEntity<CourseDTO> getCourseByUeCode(@PathVariable String ueCode) {
        CourseDTO course = courseService.getCourseByUeCode(ueCode);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable String courseId) {
        CourseDTO course = courseService.getCourse(courseId);
        return ResponseEntity.ok(course);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN', 'DEPARTMENT_HEAD')")
    public ResponseEntity<CourseDTO> createCourse(
            @RequestParam String teacherId,
            @RequestParam String ueId,
            @RequestParam(defaultValue = "3") Long duration) {
        CourseDTO course = courseService.createCourse(teacherId, ueId, duration);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/teacher/{courseId}")
    @PreAuthorize("hasAuthority('ADMIN', 'DEPARTMENT_HEAD')")
    public ResponseEntity<CourseDTO> updateCourseTeacher(
            @PathVariable String courseId,
            @RequestParam String teacherId) {
        CourseDTO course = courseService.updateCourseTeacher(courseId, teacherId);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/ue/{courseId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DEPARTMENT_HEAD')")
    public ResponseEntity<CourseDTO> updateCourseUe(
            @PathVariable String courseId,
            @RequestParam String ueId) {
        CourseDTO course = courseService.updateCourseUe(courseId, ueId);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/duration/{courseId}")
    @PreAuthorize("hasAuthority('ADMIN', 'DEPARTMENT_HEAD')")
    public ResponseEntity<CourseDTO> updateCourseDuration(
            @PathVariable String courseId,
            @RequestParam Long duration) {
        CourseDTO course = courseService.updateCourseDuration(courseId, duration);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}
