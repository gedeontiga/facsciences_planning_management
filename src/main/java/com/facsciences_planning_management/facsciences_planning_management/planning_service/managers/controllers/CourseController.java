package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.CourseService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<Page<CourseDTO>> getAllCourses(
            @PageableDefault(size = 10) Pageable page) {
        Page<CourseDTO> courses = courseService.getAllCourses(page);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/ue/{ueId}")
    public ResponseEntity<CourseDTO> getCourseByUeId(@PathVariable String ueId) {
        CourseDTO course = courseService.getCourseByUe(ueId);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable String courseId) {
        CourseDTO course = courseService.getCourse(courseId);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/level/{levelId}")
    public ResponseEntity<Page<CourseDTO>> getCourseByLevel(@PathVariable String levelId,
            @PageableDefault(size = 10) Pageable page) {
        return ResponseEntity.ok(courseService.getCourseByLevel(levelId, page));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DEPARTMENT_HEAD')")
    public ResponseEntity<CourseDTO> createCourse(
            @Valid @RequestBody CourseRequest request) {
        CourseDTO response = courseService.createCourse(request);
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/teacher/{courseId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DEPARTMENT_HEAD')")
    public ResponseEntity<CourseDTO> updateCourseTeacher(
            @PathVariable String courseId,
            @Valid @RequestBody String teacherId,
            @Valid @RequestBody String departmentId) {
        CourseDTO course = courseService.updateCourseTeacher(courseId, teacherId, departmentId);
        return ResponseEntity.ok(course);
    }

    @PatchMapping("/ue/{courseId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DEPARTMENT_HEAD')")
    public ResponseEntity<CourseDTO> updateCourseUe(
            @PathVariable String courseId,
            @Valid @RequestBody String ueId) {
        CourseDTO course = courseService.updateCourseUe(courseId, ueId);
        return ResponseEntity.ok(course);
    }

    @PatchMapping("/duration/{courseId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DEPARTMENT_HEAD')")
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
