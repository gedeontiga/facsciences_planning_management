package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.AcademicYearDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.TimetableService;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidAcademicYear;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public ResponseEntity<TimetableDTO> createTimetable(@Valid @RequestBody TimetableRequest request) {
        if (request.sessionType().equals(SessionType.COURSE)) {
            return ResponseEntity.status(201).body(timetableService.createTimetableForCourse(
                    request.levelId(),
                    request.academicYear(),
                    request.semester(),
                    request.sessionType()));
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

    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARY')")
    public ResponseEntity<TimetableDTO> generateTimetableForLevel(@Valid @RequestBody TimetableRequest request) {
        if (request.sessionType() != null && !request.sessionType().equals(SessionType.COURSE)) {
            throw new IllegalArgumentException("Exam timetable is not yet supported");
        }
        return ResponseEntity.status(201).body(timetableService.generateTimetableForLevel(
                request.academicYear(),
                request.semester(),
                request.levelId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimetableDTO> getTimetableById(@NonNull @PathVariable String id) {
        return ResponseEntity.ok(timetableService.getTimetableById(id));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<Page<TimetableDTO>> getTimetableByBranchAndSemester(
            @NonNull @PathVariable String branchId,
            @ValidAcademicYear @RequestParam String academicYear,
            @Valid @RequestParam SessionType sessionType,
            @PageableDefault(size = 10, sort = "academicYear") Pageable page) {
        return ResponseEntity
                .ok(timetableService.getTimetablesByBranch(academicYear, branchId, sessionType, page));
    }

    @GetMapping("/level/{levelId}")
    public ResponseEntity<TimetableDTO> getTimetableByLevelAndSemester(
            @NonNull @PathVariable String levelId,
            @ValidAcademicYear @RequestParam String academicYear,
            @RequestParam Semester semester,
            @RequestParam SessionType sessionType) {
        return ResponseEntity
                .ok(timetableService.getTimetableByLevelAndSemester(academicYear, levelId, semester.name(),
                        sessionType));
    }

    @PostMapping("/year")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> createAcademicYear(@Valid @RequestBody AcademicYearDTO academicYear) {
        return ResponseEntity.ok(timetableService.createAcademicYear(academicYear.label()));
    }

    @GetMapping("/year")
    public ResponseEntity<List<String>> getAllAcademicYears() {
        return ResponseEntity.ok(timetableService.getAllAcademicYears());
    }

    @GetMapping("/session")
    public ResponseEntity<List<String>> getSessionTypes() {
        return ResponseEntity.ok(timetableService.getSessionTypes());
    }

    @GetMapping("/timeslots")
    public ResponseEntity<List<TimeSlotDTO>> getCoursesTimeSlots() {
        return ResponseEntity.ok(timetableService.getCoursesTimeSlots());
    }

    @GetMapping("/semesters/course")
    public ResponseEntity<List<String>> getSemestersForCourseTimetable() {
        return ResponseEntity.ok(timetableService.getSemestersForCourseTimetable());
    }

    @GetMapping("/semesters/exam")
    public ResponseEntity<List<String>> getSemestersForExamTimetable() {
        return ResponseEntity.ok(timetableService.getSemestersForExamTimetable());
    }
}
