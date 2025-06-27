package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.DayOfWeek;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot;
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
public final class CourseScheduling extends Scheduling {
    @Indexed
    protected DayOfWeek day;
    @DocumentReference(collection = "courses")
    private Course assignedCourse;
    private TimeSlot.CourseTimeSlot timeSlot;

    @Override
    public CourseSchedulingDTO toDTO() {
        return CourseSchedulingDTO.fromEntity(this);
    }
}
