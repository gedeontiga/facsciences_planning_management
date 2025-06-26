package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "academic_years")
public class AcademicYear {
    @Id
    private String id;
    @Indexed(unique = true)
    private final String label;
}
