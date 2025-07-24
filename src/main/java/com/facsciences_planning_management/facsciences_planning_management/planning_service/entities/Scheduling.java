package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "schedulings")
public abstract class Scheduling {
    @Id
    private String id;
    @DocumentReference(collection = "rooms")
    private Room room;
    @DocumentReference(lazy = true, collection = "timetables")
    private Timetable timetable;
    private Long headCount;
    private String headCountLabel;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Builder.Default
    private boolean active = true;
    @Indexed
    private String branchId;
    @Indexed
    private String levelId;
    // private final String facultyId = room.getFacultyId();

    public abstract SchedulingDTO toDTO();
}
