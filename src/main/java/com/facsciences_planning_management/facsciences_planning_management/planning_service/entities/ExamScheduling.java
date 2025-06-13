package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
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
    @DocumentReference(lazy = true, collection = "users")
    private Users proctor;
    private LocalDateTime sessionDate;
    @DocumentReference(lazy = true, collection = "ues")
    private Ue ue;

    @Override
    public ExamSchedulingDTO toDTO() {
        return ExamSchedulingDTO.fromExamScheduling(this);
    }
}
