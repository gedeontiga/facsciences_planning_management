package com.facsciences_planning_management.facsciences_planning_management.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Department;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.TeacherDTO;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Teacher extends Users {
    @DocumentReference(collection = "departments")
    private Department department;

    public TeacherDTO toDTO() {
        return TeacherDTO.from(this);
    }
}
