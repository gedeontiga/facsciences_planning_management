package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Level;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Scheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.LevelRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.RoomRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.TimetableRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.TimetableService;

import java.time.DayOfWeek;
import java.time.Year;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {
        private final TimetableRepository timetableRepository;
        private final TimetableSolverService timetableSolverService;
        private final CourseRepository courseRepository;
        private final RoomRepository roomRepository;
        private final LevelRepository levelRepository;
        private final CourseSchedulingRepository courseSchedulingRepository;

        @Override
        @Transactional
        public TimetableDTO generateTimetableForLevel(String academicYear, String semester, String levelId,
                        SessionType sessionType) {
                Level level = levelRepository.findById(levelId)
                                .orElseThrow(() -> new CustomBusinessException("Level not found with id: " + levelId));

                // 1. Gather all necessary data for the solver
                List<Course> coursesToSchedule = courseRepository.findByUe_Level_IdAndObsoleteIsFalse(levelId);
                if (coursesToSchedule.isEmpty()) {
                        throw new IllegalStateException(
                                        "No active courses found for level " + level.getName() + " to schedule.");
                }

                List<Room> availableRooms = roomRepository.findAllByAvailabilityTrue();
                if (availableRooms.isEmpty()) {
                        throw new IllegalStateException("No available rooms for scheduling.");
                }

                List<DayOfWeek> days = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                DayOfWeek.THURSDAY,
                                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);
                List<CourseTimeSlot> timeSlots = Arrays.asList(CourseTimeSlot.values());

                // 2. Call the solver
                List<CourseScheduling> solvedSchedules = timetableSolverService.solve(coursesToSchedule, availableRooms,
                                days,
                                timeSlots);

                // 3. Create and save the new timetable and schedules
                // First, remove any existing timetable for this combination to avoid duplicates
                timetableRepository
                                .findByAcademicYearAndSemesterAndLevelIdAndSessionTypeAndUsedTrue(
                                                academicYear, semester,
                                                levelId,
                                                sessionType)
                                .ifPresent(existing -> {
                                        courseSchedulingRepository.deleteById(existing.getId());
                                        timetableRepository.deleteById(existing.getId());
                                });

                Timetable newTimetable = Timetable.builder()
                                .academicYear(academicYear)
                                .semester(semester)
                                .level(level)
                                .schedules(new HashSet<>())
                                .build();
                Timetable savedTimetable = timetableRepository.save(newTimetable);

                // Link schedules to the new timetable and save them
                Set<Scheduling> finalSchedules = new HashSet<>();
                for (CourseScheduling schedule : solvedSchedules) {
                        schedule.setTimetable(savedTimetable);
                        finalSchedules.add(courseSchedulingRepository.save(schedule));
                }

                savedTimetable.setSchedules(finalSchedules);
                return timetableRepository.save(savedTimetable).toDTO();
        }

        // Other methods from the interface...

        @Override
        public TimetableDTO getTimetableById(String id) {
                return timetableRepository.findById(id)
                                .map(Timetable::toDTO)
                                .orElseThrow(() -> new CustomBusinessException("Timetable not found with id: " + id));
        }

        @Override
        public Page<TimetableDTO> getTimetablesByBranch(String academicYear,
                        String branchId, SessionType sessionType, Pageable page) {
                return timetableRepository
                                .findByAcademicYearAndSessionTypeAndUsedTrueAndLevel_Branch_Id(academicYear,
                                                sessionType, branchId, page)
                                .map(Timetable::toDTO);
        }

        @Override
        public TimetableDTO getTimetableByLevelAndSemester(String academicYear, String levelId, String semester,
                        SessionType sessionType) {
                return timetableRepository
                                .findByAcademicYearAndSemesterAndLevelIdAndSessionTypeAndUsedTrue(
                                                academicYear, semester,
                                                levelId,
                                                sessionType)
                                .map(Timetable::toDTO)
                                .orElseThrow(() -> new CustomBusinessException(
                                                String.format("Timetable not found for Level %s, Year %s, Semester %s",
                                                                levelId,
                                                                academicYear,
                                                                semester)));
        }

        @Override
        public List<Year> getAllAcademicYears() {
                return timetableRepository.findDistinctByAcademicYearOrderByAcademicYearDesc()
                                .stream()
                                .map(Year::of)
                                .collect(Collectors.toList());
        }

        @Override
        public List<String> getSessionTypes() {
                return Arrays.stream(SessionType.values()).map(Enum::name).collect(Collectors.toList());
        }

        // Implement other get, update, delete methods as straightforward repository
        // calls
}