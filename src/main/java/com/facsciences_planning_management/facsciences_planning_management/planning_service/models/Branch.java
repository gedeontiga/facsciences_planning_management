package com.facsciences_planning_management.facsciences_planning_management.planning_service.models;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "branches")
public class Branch {
    @Id
    private String id;
    private String name;
    private String code;

    @Singular
    @DocumentReference(collection = "levels")
    private Set<Level> levels;

    @DocumentReference(collection = "departments")
    private Department department;

    @DocumentReference(collection = "faculties")
    private Faculty faculty;
}
