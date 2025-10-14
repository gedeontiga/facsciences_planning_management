package com.facsciencesuy1.planning_management.entities;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciencesuy1.planning_management.dtos.ExamSchedulingDTO;
import com.facsciencesuy1.planning_management.entities.types.TimeSlot;

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
@EqualsAndHashCode(callSuper = false)
public class ExamScheduling extends Scheduling {
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
