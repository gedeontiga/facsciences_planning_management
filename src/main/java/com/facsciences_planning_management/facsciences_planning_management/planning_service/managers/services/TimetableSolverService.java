package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TimetableSolverService {
    // This record is a good representation of a single potential schedule slot.
    private record ScheduleVar(Course course, Room room, DayOfWeek day, CourseTimeSlot timeSlot) {
    }

    // A helper record to uniquely identify a resource slot (ResourceID, Day,
    // TimeSlot)
    private record ResourceSlot(String resourceId, DayOfWeek day, CourseTimeSlot timeSlot) {
    }

    /**
     * Solves the timetabling problem for a given set of courses, ensuring the
     * solution
     * does not conflict with any pre-existing schedules from other timetables.
     *
     * @param coursesToSchedule The list of courses that must be scheduled.
     * @param rooms             All available rooms in the system.
     * @param days              The days of the week available for scheduling.
     * @param timeSlots         The time slots available during a day.
     * @param existingSchedules A list of all active schedules that must be avoided.
     * @return A list of newly generated CourseScheduling entities.
     */
    public List<CourseScheduling> solve(List<Course> coursesToSchedule, List<Room> rooms, List<DayOfWeek> days,
            List<CourseTimeSlot> timeSlots, List<CourseScheduling> existingSchedules) {
        CpModel model = new CpModel();
        Map<ScheduleVar, Literal> variables = new HashMap<>();

        // --- Pre-process Existing Schedules for Efficient Lookup ---
        log.info("Processing {} existing schedules to identify resource conflicts.", existingSchedules.size());
        Set<ResourceSlot> occupiedRoomSlots = new HashSet<>();
        Set<ResourceSlot> occupiedTeacherSlots = new HashSet<>();
        Set<ResourceSlot> occupiedLevelSlots = new HashSet<>();

        for (CourseScheduling existing : existingSchedules) {
            ResourceSlot roomSlot = new ResourceSlot(existing.getRoom().getId(), existing.getDay(),
                    existing.getTimeSlot());
            occupiedRoomSlots.add(roomSlot);

            ResourceSlot teacherSlot = new ResourceSlot(existing.getAssignedCourse().getTeacher().getId(),
                    existing.getDay(), existing.getTimeSlot());
            occupiedTeacherSlots.add(teacherSlot);

            ResourceSlot levelSlot = new ResourceSlot(existing.getAssignedCourse().getUe().getLevel().getId(),
                    existing.getDay(), existing.getTimeSlot());
            occupiedLevelSlots.add(levelSlot);
        }
        log.info("Found {} unique room conflicts, {} teacher conflicts, {} level conflicts.",
                occupiedRoomSlots.size(), occupiedTeacherSlots.size(), occupiedLevelSlots.size());

        // --- Create Solver Variables ---
        // Create a boolean variable for each valid, non-conflicting possibility.
        for (Course course : coursesToSchedule) {
            for (Room room : rooms) {
                // Hard Constraint: Room must have sufficient capacity.
                if (course.getUe().getLevel().getTotalNumberOfStudents() > room.getCapacity()) {
                    continue;
                }

                for (DayOfWeek day : days) {
                    for (CourseTimeSlot timeSlot : timeSlots) {
                        // Hard Constraint: Do not create a variable if the slot is already taken by any
                        // resource.
                        if (occupiedRoomSlots.contains(new ResourceSlot(room.getId(), day, timeSlot)) ||
                                occupiedTeacherSlots
                                        .contains(new ResourceSlot(course.getTeacher().getId(), day, timeSlot))
                                ||
                                occupiedLevelSlots
                                        .contains(new ResourceSlot(course.getUe().getLevel().getId(), day, timeSlot))) {
                            continue; // This slot is impossible, so we don't even create a variable for it.
                        }

                        String varName = String.format("x_%s_%s_%s_%s", course.getId(), room.getId(), day,
                                timeSlot.name());
                        variables.put(new ScheduleVar(course, room, day, timeSlot), model.newBoolVar(varName));
                    }
                }
            }
        }

        // --- Define Constraints on New Schedules ---

        log.info("Applying Constraint 1: Each course must be scheduled exactly once.");
        for (Course course : coursesToSchedule) {
            List<Literal> assignments = variables.keySet().stream()
                    .filter(var -> var.course().equals(course))
                    .map(variables::get)
                    .collect(Collectors.toList());

            // This is a crucial check. If a course has no possible assignments, the problem
            // is infeasible.
            if (assignments.isEmpty()) {
                log.error(
                        "Course '{}' cannot be scheduled. No available slots found due to hard constraints (capacity, existing schedules).",
                        course.getUe().getName());
                throw new IllegalStateException("Problem is infeasible: Course '" + course.getUe().getName()
                        + "' has no valid scheduling options.");
            }
            model.addExactlyOne(assignments);
        }

        log.info("Applying Constraint 2: A student level cannot have two courses at the same time.");
        for (DayOfWeek day : days) {
            for (CourseTimeSlot timeSlot : timeSlots) {
                List<Literal> levelAssignments = variables.keySet().stream()
                        .filter(var -> var.day().equals(day) && var.timeSlot().equals(timeSlot))
                        .map(variables::get)
                        .collect(Collectors.toList());
                if (levelAssignments.size() > 1) {
                    model.addAtMostOne(levelAssignments);
                }
            }
        }

        log.info("Applying Constraint 3: A teacher cannot teach two courses at the same time.");
        for (Users teacher : coursesToSchedule.stream().map(Course::getTeacher).collect(Collectors.toSet())) {
            for (DayOfWeek day : days) {
                for (CourseTimeSlot timeSlot : timeSlots) {
                    List<Literal> teacherAssignments = variables.keySet().stream()
                            .filter(var -> var.course().getTeacher().equals(teacher) && var.day().equals(day)
                                    && var.timeSlot().equals(timeSlot))
                            .map(variables::get)
                            .collect(Collectors.toList());
                    if (teacherAssignments.size() > 1) {
                        model.addAtMostOne(teacherAssignments);
                    }
                }
            }
        }

        log.info("Applying Constraint 4: A room cannot host two courses at the same time.");
        for (Room room : rooms) {
            for (DayOfWeek day : days) {
                for (CourseTimeSlot timeSlot : timeSlots) {
                    List<Literal> roomAssignments = variables.keySet().stream()
                            .filter(var -> var.room().equals(room) && var.day().equals(day)
                                    && var.timeSlot().equals(timeSlot))
                            .map(variables::get)
                            .collect(Collectors.toList());
                    if (roomAssignments.size() > 1) {
                        model.addAtMostOne(roomAssignments);
                    }
                }
            }
        }

        // --- Define Objective Function (Soft Constraint) ---
        // (This part remains the same)
        log.info("Defining Objective Function: Maximize morning schedules.");
        Map<CourseTimeSlot, Integer> slotWeights = Map.of(CourseTimeSlot.COURSE_SLOT_1, 10,
                CourseTimeSlot.COURSE_SLOT_2, 8,
                CourseTimeSlot.COURSE_SLOT_3, 4, CourseTimeSlot.COURSE_SLOT_4, 2, CourseTimeSlot.COURSE_SLOT_5, -5);
        LinearExprBuilder objective = LinearExpr.newBuilder();
        variables.forEach((var, literal) -> {
            int weight = slotWeights.getOrDefault(var.timeSlot(), 0);
            objective.addTerm(literal, weight);
        });
        model.maximize(objective);

        // --- Solve the Model ---
        log.info("Starting solver...");
        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(60.0); // Increased timeout for potentially harder problems
        CpSolverStatus status = solver.solve(model);
        log.info("Solver finished with status: {}", status);

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            List<CourseScheduling> generatedSchedules = new ArrayList<>();
            log.info("Solution found. Objective value: " + solver.objectiveValue());
            for (Map.Entry<ScheduleVar, Literal> entry : variables.entrySet()) {
                if (solver.booleanValue(entry.getValue())) {
                    ScheduleVar key = entry.getKey();
                    generatedSchedules.add(CourseScheduling.builder().assignedCourse(key.course()).room(key.room())
                            .day(key.day()).timeSlot(key.timeSlot()).build());
                }
            }
            return generatedSchedules;
        } else {
            log.error("No solution found for the timetabling problem. Status: {}", status);
            throw new RuntimeException(
                    "Could not generate a timetable. The problem is infeasible, likely because existing schedules create irresolvable conflicts for required resources (rooms, teachers) or there are not enough resources to schedule all courses.");
        }
    }
}