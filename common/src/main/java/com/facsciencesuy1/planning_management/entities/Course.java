package com.facsciencesuy1.planning_management.entities;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciencesuy1.planning_management.dtos.CourseDTO;
import com.facsciencesuy1.planning_management.entities.types.Semester;

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
    @DocumentReference(collection = "ues")
    private Ue ue;
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
    @Indexed
    private String levelId;
    private String branchId;
    @Indexed
    private Semester semester;

    public CourseDTO toDTO() {
        return CourseDTO.fromCourse(this);
    }
}
