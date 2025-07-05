package com.facsciences_planning_management.facsciences_planning_management.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Teacher extends Users {
    @DocumentReference(collection = "departments")
    private String departmentId;
}
