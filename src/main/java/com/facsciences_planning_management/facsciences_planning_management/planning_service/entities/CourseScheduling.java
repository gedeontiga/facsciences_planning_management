package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.DayOfWeek;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "course_schedules")
public final class CourseScheduling extends Scheduling {
    protected DayOfWeek day;
    @DocumentReference(lazy = true, collection = "courses")
    private Course assignedCourse;

    @Override
    public CourseSchedulingDTO toDTO() {
        return CourseSchedulingDTO.fromEntity(this);
    }
}
