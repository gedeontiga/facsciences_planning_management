package com.facsciencesuy1.planning_management.entities;

import java.time.DayOfWeek;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciencesuy1.planning_management.dtos.CourseSchedulingDTO;
import com.facsciencesuy1.planning_management.entities.types.TimeSlot;

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
public class CourseScheduling extends Scheduling {
    @Indexed
    protected DayOfWeek day;
    @DocumentReference(collection = "courses")
    private Course assignedCourse;
    private TimeSlot.CourseTimeSlot timeSlot;
    @Indexed
    private String teacherId;

    @Override
    public CourseSchedulingDTO toDTO() {
        return CourseSchedulingDTO.fromEntity(this);
    }
}
