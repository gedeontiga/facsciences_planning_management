package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "exam_schedules")
@EqualsAndHashCode(callSuper = false)
public final class ExamScheduling extends Scheduling {
    @DocumentReference(collection = "users")
    private Users proctor;
    private LocalDate sessionDate;
    @DocumentReference(collection = "ues")
    private Ue ue;
    private TimeSlot.ExamTimeSlot timeSlot;

    @Override
    public ExamSchedulingDTO toDTO() {
        return ExamSchedulingDTO.fromExamScheduling(this);
    }
}
