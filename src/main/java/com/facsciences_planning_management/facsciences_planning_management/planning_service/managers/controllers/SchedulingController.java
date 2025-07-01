package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.SchedulingService;

@Validated
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class SchedulingController {

    @Qualifier("courseSchedulingService")
    private final SchedulingService<CourseSchedulingDTO> courseSchedulingService;
    @Qualifier("examSchedulingService")
    private final SchedulingService<ExamSchedulingDTO> examSchedulingService;

    @PostMapping("/course")
    public ResponseEntity<CourseSchedulingDTO> createCourseSchedule(
            @RequestBody CourseSchedulingDTO request) {
        CourseSchedulingDTO response = courseSchedulingService.createScheduling(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/exam")
    public ResponseEntity<ExamSchedulingDTO> createExamSchedule(
            @RequestBody ExamSchedulingDTO request) {
        ExamSchedulingDTO response = examSchedulingService.createScheduling(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<CourseSchedulingDTO> updateCourseSchedule(
            @PathVariable String id,
            @RequestBody CourseSchedulingDTO request) {
        CourseSchedulingDTO updatedSchedule = courseSchedulingService.updateScheduling(id, request);
        return ResponseEntity.ok(updatedSchedule);
    }

    @PutMapping("/exams/{id}")
    public ResponseEntity<ExamSchedulingDTO> updateExamSchedule(
            @PathVariable String id,
            @RequestBody ExamSchedulingDTO request) {
        ExamSchedulingDTO updatedSchedule = examSchedulingService.updateScheduling(id, request);
        return ResponseEntity.ok(updatedSchedule);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseSchedulingDTO> getCourseSchedule(@PathVariable String id) {
        CourseSchedulingDTO scheduling = courseSchedulingService.getScheduling(id);
        return ResponseEntity.ok(scheduling);
    }

    @GetMapping("/exams/{id}")
    public ResponseEntity<ExamSchedulingDTO> getExamSchedule(@PathVariable String id) {
        ExamSchedulingDTO scheduling = examSchedulingService.getScheduling(id);
        return ResponseEntity.ok(scheduling);
    }

}
