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
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.TimetableService;

import java.time.Year;
import java.util.List;

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
            @PageableDefault(size = 10) Pageable page) {
        return ResponseEntity.ok(timetableService.getTimetablesByBranch(academicYear, branchId, sessionType, page));
    }

    @GetMapping("/academic-years")
    public ResponseEntity<List<Year>> getAllAcademicYears() {
        List<Year> years = timetableService.getAllAcademicYears();
        return ResponseEntity.ok(years);
    }

    @GetMapping("/session-types")
    public ResponseEntity<List<String>> getSessionTypes() {
        List<String> sessionTypes = timetableService.getSessionTypes();
        return ResponseEntity.ok(sessionTypes);
    }
}
