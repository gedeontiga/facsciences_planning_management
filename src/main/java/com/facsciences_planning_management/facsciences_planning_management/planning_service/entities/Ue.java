package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeDTO;

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
