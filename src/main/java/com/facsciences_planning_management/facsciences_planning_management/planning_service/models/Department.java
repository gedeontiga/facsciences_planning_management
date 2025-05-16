package com.facsciences_planning_management.facsciences_planning_management.planning_service.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "departments")
public class Department {
    @Id
    private String id;
    private String name;
    private String code;
}
