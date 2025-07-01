package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.TimetableService;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.AcademicYearFormat;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;

@Validated
@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public ResponseEntity<TimetableDTO> createTimetable(@RequestBody TimetableRequest request) {
        if (request.sessionType().equals(SessionType.COURSE)) {
            return ResponseEntity.status(201).body(timetableService.generateTimetableForLevel(request.academicYear(),
                    request.semester(), request.levelId()));
        } else {
            if (List.of(SessionType.values()).contains(request.sessionType())) {
                return ResponseEntity.status(201).body(timetableService.createTimetableForExam(
                        request.levelId(),
                        request.academicYear(),
                        request.semester(),
                        request.sessionType()));
            }
            throw new IllegalArgumentException("Invalid session type: " + request.sessionType());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimetableDTO> getTimetableById(@PathVariable String id) {
        return ResponseEntity.ok(timetableService.getTimetableById(id));
    }

    @GetMapping
    public ResponseEntity<Page<TimetableDTO>> getTimetableByLevelAndSemester(
            @RequestParam @AcademicYearFormat String academicYear,
            @RequestParam SessionType sessionType,
            @RequestParam String branchId,
            @PageableDefault(size = 10, sort = "academicYear") Pageable page) {
        return ResponseEntity
                .ok(timetableService.getTimetablesByBranch(academicYear, branchId, sessionType, page));
    }

    @PostMapping("/create-year")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> createAcademicYear(@RequestParam @AcademicYearFormat String academicYear) {
        return ResponseEntity.ok(timetableService.createAcademicYear(academicYear));
    }

    @GetMapping("/academic-years")
    public ResponseEntity<List<String>> getAllAcademicYears() {
        return ResponseEntity.ok(timetableService.getAllAcademicYears());
    }

    @GetMapping("/session-types")
    public ResponseEntity<List<String>> getSessionTypes() {
        return ResponseEntity.ok(timetableService.getSessionTypes());
    }

    @GetMapping("/time-slots")
    public ResponseEntity<List<TimeSlotDTO>> getCoursesTimeSlots() {
        return ResponseEntity.ok(timetableService.getCoursesTimeSlots());
    }

    @GetMapping("/semesters")
    public ResponseEntity<List<String>> getSemestersForCourseTimetable() {
        return ResponseEntity.ok(timetableService.getSemestersForCourseTimetable());
    }

    @GetMapping("/semesters-exam")
    public ResponseEntity<List<String>> getSemestersForExamTimetable() {
        return ResponseEntity.ok(timetableService.getSemestersForExamTimetable());
    }
}
