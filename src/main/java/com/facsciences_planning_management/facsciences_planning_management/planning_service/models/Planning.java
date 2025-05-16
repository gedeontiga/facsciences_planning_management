package com.facsciences_planning_management.facsciences_planning_management.planning_service.models;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.PlanningDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "plannings")
public class Planning {
    @Id
    private String id;
    @Singular
    @DocumentReference(collection = "schedules")
    private Set<Scheduling> schedules;
    private Year academicYear;
    private String semester;
    private LocalDateTime createdAt;

    public PlanningDTO toDTO() {
        return PlanningDTO.fromPlanning(this);
    }
}
