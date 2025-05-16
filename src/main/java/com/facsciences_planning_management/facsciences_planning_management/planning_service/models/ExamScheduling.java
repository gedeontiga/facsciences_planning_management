package com.facsciences_planning_management.facsciences_planning_management.planning_service.models;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Users;

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
@Document(collection = "exam_scheduling")
@EqualsAndHashCode(callSuper = false)
public final class ExamScheduling extends Scheduling {
    @DocumentReference(collection = "users")
    private Users proctor;
    private LocalDateTime sessionDate;

    @Override
    public SchedulingDTO toDTO() {
        return ExamSchedulingDTO.fromEntity(this);
    }
}
