package com.facsciencesuy1.planning_management.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciencesuy1.planning_management.dtos.LevelDTO;

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
    private Long headCount;

    @DocumentReference(lazy = true, collection = "branches")
    private Branch branch;

    public LevelDTO toDTO() {
        return LevelDTO.fromLevel(this);
    }
}
