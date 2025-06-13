package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.Duration;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "courses")
public class Course {
    @Id
    private String id;
    @Indexed(unique = true)
    @DocumentReference(lazy = true, collection = "ues")
    private Ue ue;
    @DocumentReference(lazy = true, collection = "users")
    private Users teacher;
    private Duration duration;

    public CourseDTO toDTO() {
        return CourseDTO.fromCourse(this);
    }
}
