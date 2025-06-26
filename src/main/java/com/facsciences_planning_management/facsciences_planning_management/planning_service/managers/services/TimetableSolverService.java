package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Level;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TimetableSolverService {

    // Record to hold the tuple for variable mapping
    private record ScheduleVar(Course course, Room room, DayOfWeek day, CourseTimeSlot timeSlot) {
    }

    public List<CourseScheduling> solve(List<Course> courses, List<Room> rooms, List<DayOfWeek> days,
            List<CourseTimeSlot> timeSlots) {
        CpModel model = new CpModel();

        // Create a map to hold our boolean variables: x(c, r, d, t)
        Map<ScheduleVar, Literal> variables = new HashMap<>();
        for (Course course : courses) {
            for (Room room : rooms) {
                for (DayOfWeek day : days) {
                    for (CourseTimeSlot timeSlot : timeSlots) {
                        // Hard constraint: Room Capacity
                        if (course.getUe().getLevel().getTotalNumberOfStudents() <= room.getCapacity()) {
                            String varName = String.format("x_%s_%s_%s_%s", course.getId(), room.getId(), day,
                                    timeSlot.name());
                            variables.put(
                                    new ScheduleVar(course, room, day, timeSlot),
                                    model.newBoolVar(varName));
                        }
                    }
                }
            }
        }

        // --- HARD CONSTRAINTS ---

        // Constraint 1: Each course must be scheduled exactly once.
        log.info("Applying Constraint 1: Each course scheduled once.");
        for (Course course : courses) {
            List<Literal> assignments = variables.entrySet().stream()
                    .filter(entry -> entry.getKey().course().equals(course))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
            if (!assignments.isEmpty()) {
                model.addExactlyOne(assignments);
            }
        }

        // Constraint 2: A level cannot have two classes at the same time.
        log.info("Applying Constraint 2: No conflicts for levels.");
        // --- FIX APPLIED HERE ---
        // Use a merge function to handle duplicate keys.
        Map<String, Level> levels = courses.stream()
                .map(c -> c.getUe().getLevel())
                .collect(Collectors.toMap(Level::getId, l -> l, (existing, replacement) -> existing));

        for (Level level : levels.values()) {
            for (DayOfWeek day : days) {
                for (CourseTimeSlot timeSlot : timeSlots) {
                    List<Literal> assignments = variables.entrySet().stream()
                            .filter(entry -> entry.getKey().course().getUe().getLevel().getId().equals(level.getId()) &&
                                    entry.getKey().day().equals(day) &&
                                    entry.getKey().timeSlot().equals(timeSlot))
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    if (assignments.size() > 1) {
                        model.addAtMostOne(assignments);
                    }
                }
            }
        }

        // Constraint 3: A teacher cannot teach two classes at the same time.
        log.info("Applying Constraint 3: No conflicts for teachers.");
        // --- FIX APPLIED HERE ---
        // Use a merge function to handle duplicate keys.
        Map<String, Users> teachers = courses.stream()
                .map(Course::getTeacher)
                .collect(Collectors.toMap(Users::getId, t -> t, (existing, replacement) -> existing));

        for (Users teacher : teachers.values()) {
            for (DayOfWeek day : days) {
                for (CourseTimeSlot timeSlot : timeSlots) {
                    List<Literal> assignments = variables.entrySet().stream()
                            .filter(entry -> entry.getKey().course().getTeacher().getId().equals(teacher.getId()) &&
                                    entry.getKey().day().equals(day) &&
                                    entry.getKey().timeSlot().equals(timeSlot))
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    if (assignments.size() > 1) {
                        model.addAtMostOne(assignments);
                    }
                }
            }
        }

        // Constraint 4: A room cannot be used for two classes at the same time.
        log.info("Applying Constraint 4: No conflicts for rooms.");
        for (Room room : rooms) {
            for (DayOfWeek day : days) {
                for (CourseTimeSlot timeSlot : timeSlots) {
                    List<Literal> assignments = variables.entrySet().stream()
                            .filter(entry -> entry.getKey().room().equals(room) &&
                                    entry.getKey().day().equals(day) &&
                                    entry.getKey().timeSlot().equals(timeSlot))
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    if (assignments.size() > 1) {
                        model.addAtMostOne(assignments);
                    }
                }
            }
        }

        // --- OBJECTIVE FUNCTION (SOFT CONSTRAINTS) ---
        log.info("Defining Objective Function: Maximize morning schedules.");
        // Weights: Higher for earlier slots, penalty for the last slot
        Map<CourseTimeSlot, Integer> slotWeights = Map.of(
                CourseTimeSlot.COURSE_SLOT_1, 10,
                CourseTimeSlot.COURSE_SLOT_2, 8,
                CourseTimeSlot.TD_SLOT_1, 10,
                CourseTimeSlot.TD_SLOT_2, 8,
                CourseTimeSlot.COURSE_SLOT_3, 4,
                CourseTimeSlot.TD_SLOT_3, 4,
                CourseTimeSlot.COURSE_SLOT_4, 2,
                CourseTimeSlot.TD_SLOT_4, 2,
                CourseTimeSlot.COURSE_SLOT_5, -5 // Avoid this slot
        );

        LinearExprBuilder objective = LinearExpr.newBuilder();
        variables.forEach((var, literal) -> {
            int weight = slotWeights.getOrDefault(var.timeSlot(), 0);
            objective.addTerm(literal, weight);
        });
        model.maximize(objective);

        // --- SOLVE ---
        log.info("Starting solver...");
        CpSolver solver = new CpSolver();
        // Set a time limit for the solver to avoid it running indefinitely
        solver.getParameters().setMaxTimeInSeconds(30.0);
        CpSolverStatus status = solver.solve(model);
        log.info("Solver finished with status: {}", status);

        // --- PROCESS RESULTS ---
        List<CourseScheduling> generatedSchedules = new ArrayList<>();
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            log.info("Solution found. Objective value: " + solver.objectiveValue());
            for (Map.Entry<ScheduleVar, Literal> entry : variables.entrySet()) {
                if (solver.booleanValue(entry.getValue())) {
                    ScheduleVar key = entry.getKey();
                    CourseScheduling schedule = CourseScheduling.builder()
                            .assignedCourse(key.course())
                            .room(key.room())
                            .day(key.day())
                            .timeSlot(key.timeSlot())
                            .build();
                    generatedSchedules.add(schedule);
                }
            }
        } else {
            log.error("No solution found for the timetabling problem.");
            throw new RuntimeException(
                    "Could not generate a timetable. The problem may be infeasible. Check constraints and data (e.g., not enough rooms or available time slots).");
        }

        return generatedSchedules;
    }
}