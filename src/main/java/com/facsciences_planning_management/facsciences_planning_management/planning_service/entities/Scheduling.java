package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public sealed abstract class Scheduling permits CourseScheduling, ExamScheduling {
    @Id
    private String id;
    @DocumentReference(lazy = true, collection = "rooms")
    private Room room;
    @DocumentReference(lazy = true, collection = "timetables")
    private Timetable timetable;
    private LocalTime startTime;
    private LocalTime endTime;
    private SessionType sessionType;

    public abstract SchedulingDTO toDTO();
}
