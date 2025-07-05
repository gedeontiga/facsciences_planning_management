package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.SchedulingService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class SchedulingController {

    @Qualifier("courseSchedulingService")
    private final SchedulingService<CourseSchedulingDTO, CourseSchedulingRequest> courseSchedulingService;
    @Qualifier("examSchedulingService")
    private final SchedulingService<ExamSchedulingDTO, ExamSchedulingRequest> examSchedulingService;

    @PostMapping("/course")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public ResponseEntity<CourseSchedulingDTO> createCourseSchedule(
            @Valid @RequestBody CourseSchedulingRequest request) {
        CourseSchedulingDTO response = courseSchedulingService.createScheduling(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/exam")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public ResponseEntity<ExamSchedulingDTO> createExamSchedule(
            @Valid @RequestBody ExamSchedulingRequest request) {
        ExamSchedulingDTO response = examSchedulingService.createScheduling(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/courses/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public ResponseEntity<CourseSchedulingDTO> updateCourseSchedule(
            @NonNull @PathVariable String id,
            @Valid @RequestBody CourseSchedulingRequest request) {
        CourseSchedulingDTO updatedSchedule = courseSchedulingService.updateScheduling(id, request);
        return ResponseEntity.ok(updatedSchedule);
    }

    @PutMapping("/exams/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public ResponseEntity<ExamSchedulingDTO> updateExamSchedule(
            @NonNull @PathVariable String id,
            @Valid @RequestBody ExamSchedulingRequest request) {
        ExamSchedulingDTO updatedSchedule = examSchedulingService.updateScheduling(id, request);
        return ResponseEntity.ok(updatedSchedule);
    }

    @GetMapping("/courses/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public ResponseEntity<CourseSchedulingDTO> getCourseSchedule(@PathVariable String id) {
        CourseSchedulingDTO scheduling = courseSchedulingService.getScheduling(id);
        return ResponseEntity.ok(scheduling);
    }

    @GetMapping("/exams/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public ResponseEntity<ExamSchedulingDTO> getExamSchedule(@NonNull @PathVariable String id) {
        ExamSchedulingDTO scheduling = examSchedulingService.getScheduling(id);
        return ResponseEntity.ok(scheduling);
    }

}
