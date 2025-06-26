package com.facsciences_planning_management.facsciences_planning_management.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Department;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.TeacherDTO;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Document(collection = "users")
public class Teacher extends Users {

    @Indexed
    @DocumentReference(collection = "departments")
    private Department department;

    @Override
    public String toString() {
        return "Teacher{" +
                "user=" + super.toString() +
                ", department=" + (department != null ? department.getId() : "null") +
                '}';
    }

    public TeacherDTO toDTO() {
        return TeacherDTO.from(this);
    }
}
