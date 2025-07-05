package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.entities.Teacher;
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
    @Indexed
    @DocumentReference(collection = "ues")
    private Ue ue;
    @Indexed
    @DocumentReference(collection = "users")
    private Teacher teacher;
    @Indexed
    @Builder.Default
    private boolean obsolete = false;
    private Duration duration;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public CourseDTO toDTO() {
        return CourseDTO.fromCourse(this);
    }
}
