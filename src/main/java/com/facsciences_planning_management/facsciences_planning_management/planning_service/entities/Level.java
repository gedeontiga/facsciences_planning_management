package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.LevelDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "levels")
public class Level {
    @Id
    private String id;
    private String code;
    private String name;
    private Long totalNumberOfStudents;

    @DocumentReference(lazy = true, collection = "branches")
    private Branch branch;

    public LevelDTO toDTO() {
        return LevelDTO.fromLevel(this);
    }
}
