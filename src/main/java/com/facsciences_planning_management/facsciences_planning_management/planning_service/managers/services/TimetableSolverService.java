package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Level;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;

import java.time.DayOfWeek;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class TimetableSolverService {

    private record ScheduleVar(Course course, Room room, DayOfWeek day, CourseTimeSlot timeSlot) {
    }

    private record ResourceSlot(String resourceId, DayOfWeek day, CourseTimeSlot timeSlot) {
    }

    public List<CourseScheduling> solve(List<Course> coursesToSchedule, List<Room> rooms, List<DayOfWeek> days,
            List<CourseTimeSlot> timeSlots, List<CourseScheduling> existingSchedules, Level level) {
        CpModel model = new CpModel();
        // Use LinkedHashMap to preserve insertion order, which simplifies linking
        // variables to coefficients later.
        Map<ScheduleVar, Literal> variables = new LinkedHashMap<>();

        log.info("Processing {} existing schedules to identify resource conflicts.", existingSchedules.size());
        Set<ResourceSlot> occupiedRoomSlots = new HashSet<>();
        Set<ResourceSlot> occupiedTeacherSlots = new HashSet<>();
        Set<ResourceSlot> occupiedLevelSlots = new HashSet<>();
        for (CourseScheduling existing : existingSchedules) {
            occupiedRoomSlots
                    .add(new ResourceSlot(existing.getRoom().getId(), existing.getDay(), existing.getTimeSlot()));
            occupiedTeacherSlots.add(new ResourceSlot(existing.getAssignedCourse().getTeacher().getId(),
                    existing.getDay(), existing.getTimeSlot()));
            occupiedLevelSlots.add(new ResourceSlot(existing.getAssignedCourse().getUe().getLevel().getId(),
                    existing.getDay(), existing.getTimeSlot()));
        }
        log.info("Found {} unique room conflicts, {} teacher conflicts, {} level conflicts.", occupiedRoomSlots.size(),
                occupiedTeacherSlots.size(), occupiedLevelSlots.size());

        for (Course course : coursesToSchedule) {
            for (Room room : rooms) {
                if (level.getHeadCount() > room.getCapacity())
                    continue;
                for (DayOfWeek day : days) {
                    for (CourseTimeSlot timeSlot : timeSlots) {
                        if (occupiedRoomSlots.contains(new ResourceSlot(room.getId(), day, timeSlot)) ||
                                occupiedTeacherSlots
                                        .contains(new ResourceSlot(course.getTeacher().getId(), day, timeSlot))
                                ||
                                occupiedLevelSlots.contains(new ResourceSlot(level.getId(), day, timeSlot))) {
                            continue;
                        }
                        String varName = String.format("x_%s_%s_%s_%s", course.getId(), room.getId(), day,
                                timeSlot.name());
                        variables.put(new ScheduleVar(course, room, day, timeSlot), model.newBoolVar(varName));
                    }
                }
            }
        }

        for (Course course : coursesToSchedule) {
            List<Literal> assignments = new ArrayList<>();
            for (Map.Entry<ScheduleVar, Literal> entry : variables.entrySet()) {
                if (entry.getKey().course().equals(course)) {
                    assignments.add(entry.getValue());
                }
            }
            if (assignments.isEmpty()) {
                throw new IllegalStateException("Problem is infeasible: Course '" + course.getUe().getName()
                        + "' has no valid scheduling options.");
            }
            model.addExactlyOne(assignments);
        }
        // Simplified constraint loops
        Map<Object, List<Literal>> roomSlotMap = new HashMap<>();
        Map<Object, List<Literal>> teacherSlotMap = new HashMap<>();
        Map<Object, List<Literal>> levelSlotMap = new HashMap<>();

        variables.forEach((var, literal) -> {
            levelSlotMap
                    .computeIfAbsent(new AbstractMap.SimpleEntry<>(var.day(), var.timeSlot()), k -> new ArrayList<>())
                    .add(literal);
            teacherSlotMap.computeIfAbsent(new AbstractMap.SimpleEntry<>(var.course().getTeacher(), var.day()),
                    k -> new ArrayList<>()).add(literal);
            roomSlotMap.computeIfAbsent(new AbstractMap.SimpleEntry<>(var.room(), var.day()), k -> new ArrayList<>())
                    .add(literal);
        });

        levelSlotMap.values().forEach(l -> {
            if (l.size() > 1)
                model.addAtMostOne(l);
        });
        teacherSlotMap.values().forEach(l -> {
            if (l.size() > 1)
                model.addAtMostOne(l);
        });
        roomSlotMap.values().forEach(l -> {
            if (l.size() > 1)
                model.addAtMostOne(l);
        });

        // ====================================================================================
        // START OF DEFINITIVELY CORRECTED Hierarchical Objective Function
        // ====================================================================================
        log.info(
                "Defining Hierarchical Objective Function: 1) Maximize morning schedules, 2) Minimize wasted room space.");

        Map<CourseTimeSlot, Integer> slotWeights = Map.of(
                CourseTimeSlot.COURSE_SLOT_1, 10, CourseTimeSlot.COURSE_SLOT_2, 8,
                CourseTimeSlot.COURSE_SLOT_3, 4, CourseTimeSlot.COURSE_SLOT_4, 2, CourseTimeSlot.COURSE_SLOT_5, -5);

        // --- Step 1: Create the target variables for each objective's total score ---
        int minTimeScore = coursesToSchedule.size() * -5;
        int maxTimeScore = coursesToSchedule.size() * 10;
        IntVar totalTimeScoreVar = model.newIntVar(minTimeScore, maxTimeScore, "total_time_score");

        long maxWastedSpace = rooms.stream().mapToLong(Room::getCapacity).max().orElse(0L);
        long minTotalRoomScore = -coursesToSchedule.size() * maxWastedSpace;
        long maxTotalRoomScore = 0;
        IntVar totalRoomScoreVar = model.newIntVar(minTotalRoomScore, maxTotalRoomScore, "total_room_score");

        // --- Step 2: Build the linear expressions for each objective ---
        LinearExprBuilder timeExprBuilder = LinearExpr.newBuilder();
        LinearExprBuilder roomExprBuilder = LinearExpr.newBuilder();

        for (Map.Entry<ScheduleVar, Literal> entry : variables.entrySet()) {
            ScheduleVar var = entry.getKey();
            Literal literal = entry.getValue();

            int timeCoeff = slotWeights.getOrDefault(var.timeSlot(), 0);
            long roomCoeff = -(var.room().getCapacity() - level.getHeadCount());

            timeExprBuilder.addTerm(literal, timeCoeff);
            roomExprBuilder.addTerm(literal, roomCoeff);
        }

        // --- Step 3: Add constraints to link the expressions to the target variables
        // ---
        model.addEquality(totalTimeScoreVar, timeExprBuilder.build());
        model.addEquality(totalRoomScoreVar, roomExprBuilder.build());

        // --- Step 4: Define the final hierarchical objective ---
        long secondaryScoreRange = maxTotalRoomScore - minTotalRoomScore;
        long priorityMultiplier = secondaryScoreRange + 1;

        model.maximize(LinearExpr.weightedSum(
                new IntVar[] { totalTimeScoreVar, totalRoomScoreVar }, new long[] { priorityMultiplier, 1L }));
        // ====================================================================================
        // END OF CORRECTED OBJECTIVE FUNCTION
        // ====================================================================================

        log.info("Starting solver...");
        CpSolver solver = new CpSolver();
        solver.getParameters().setNumSearchWorkers(Runtime.getRuntime().availableProcessors());
        solver.getParameters().setMaxTimeInSeconds(60.0);
        CpSolverStatus status = solver.solve(model);

        log.info("Solver finished with status: {}", status);

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            List<CourseScheduling> generatedSchedules = new ArrayList<>();
            log.info("Solution found. Objective value: " + solver.objectiveValue());
            for (Map.Entry<ScheduleVar, Literal> entry : variables.entrySet()) {
                if (solver.booleanValue(entry.getValue())) {
                    ScheduleVar key = entry.getKey();
                    generatedSchedules.add(CourseScheduling.builder()
                            .assignedCourse(key.course())
                            .room(key.room())
                            .day(key.day())
                            .timeSlot(key.timeSlot())
                            .headCount(level.getHeadCount())
                            .build());
                }
            }
            return generatedSchedules;
        } else {
            log.error("No solution found for the timetabling problem. Status: {}", status);
            throw new RuntimeException(
                    "Could not generate timetable. The problem is infeasible or unsolvable in the time limit.");
        }

    }
}