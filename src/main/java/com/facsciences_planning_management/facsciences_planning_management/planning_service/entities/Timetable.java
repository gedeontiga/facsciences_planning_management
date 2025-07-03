package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "timetables")
public class Timetable {
    @Id
    private String id;
    private String name;
    private String description;
    @Indexed
    private SessionType sessionType;
    @DocumentReference(lazy = true)
    private Set<Scheduling> schedules;
    @DocumentReference(collection = "levels")
    private Level level;
    @Indexed
    private String academicYear;
    @Indexed
    private Semester semester;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Indexed
    @Builder.Default
    private boolean used = true;

    public TimetableDTO toDTO() {
        return TimetableDTO.fromTimetable(this);
    }
}
