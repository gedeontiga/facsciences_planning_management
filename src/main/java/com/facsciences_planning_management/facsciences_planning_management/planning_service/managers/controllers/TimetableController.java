package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.components.interfaces.AcademicYearFormat;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.TimetableService;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;

@Validated
@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping("/generate/level/{levelId}")
    public ResponseEntity<TimetableDTO> generateTimetable(
            @PathVariable String levelId,
            @RequestParam @AcademicYearFormat String academicYear,
            @RequestParam String semester,
            @RequestParam SessionType sessionType) {
        TimetableDTO generatedTimetable = timetableService.generateTimetableForLevel(academicYear, semester, levelId,
                sessionType);
        return ResponseEntity.ok(generatedTimetable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimetableDTO> getTimetableById(@PathVariable String id) {
        return ResponseEntity.ok(timetableService.getTimetableById(id));
    }

    @GetMapping("/query")
    public ResponseEntity<Page<TimetableDTO>> getTimetableByLevelAndSemester(
            @RequestParam @AcademicYearFormat String academicYear,
            @RequestParam SessionType sessionType,
            @RequestParam String branchId,
            @PageableDefault(size = 10, sort = "academicYear") Pageable page) {
        return ResponseEntity.ok(timetableService.getTimetablesByBranch(academicYear, branchId, sessionType, page));
    }

    @PostMapping("/create-year")
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
