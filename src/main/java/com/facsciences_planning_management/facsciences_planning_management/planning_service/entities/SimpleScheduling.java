package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.DayOfWeek;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingDTO;

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
@Document(collection = "scheduling")
public final class SimpleScheduling extends Scheduling {
    @DocumentReference(collection = "users")
    protected Users teacher;
    protected DayOfWeek day;

    @Override
    public SimpleSchedulingDTO toDTO() {
        return SimpleSchedulingDTO.fromEntity(this);
    }
}
