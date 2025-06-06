package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "branches")
public class Branch {
    @Id
    private String id;
    private String name;
    private String code;

    @Builder.Default
    @DocumentReference(collection = "levels")
    private Set<Level> levels = new HashSet<>();

    @DocumentReference(collection = "departments")
    private Department department;

    @DocumentReference(collection = "faculties")
    private Faculty faculty;
}
