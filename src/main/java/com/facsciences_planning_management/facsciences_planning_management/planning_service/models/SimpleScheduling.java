package com.facsciences_planning_management.facsciences_planning_management.planning_service.models;

import java.time.DayOfWeek;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Users;

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
@Document(collection = "simple_scheduling")
public final class SimpleScheduling extends Scheduling {
    @DocumentReference(collection = "users")
    protected Users teacher;
    protected DayOfWeek day;

    @Override
    public SimpleSchedulingDTO toDTO() {
        return SimpleSchedulingDTO.fromEntity(this);
    }
}
