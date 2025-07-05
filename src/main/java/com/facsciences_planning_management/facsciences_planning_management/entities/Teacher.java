package com.facsciences_planning_management.facsciences_planning_management.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Document(collection = "users")
public class Teacher extends Users {
    private String departmentId;
}
