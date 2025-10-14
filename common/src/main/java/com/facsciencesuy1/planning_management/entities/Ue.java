package com.facsciencesuy1.planning_management.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciencesuy1.planning_management.dtos.UeDTO;
import com.facsciencesuy1.planning_management.entities.types.Semester;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ues")
public class Ue {
    @Id
    private String id;
    private String name;
    private String code;
    private Integer credits;
    private String category;
    private Integer hourlyCharge;
    private Semester semester;
    @Builder.Default
    private Boolean assigned = false;

    @DocumentReference(lazy = true, collection = "levels")
    private Level level;

    public UeDTO toDTO() {
        return UeDTO.fromUe(this);
    }
}
