package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationresponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "teacher_requests")
public class Reservation {
    @Id
    private String id;
    @DocumentReference(lazy = true, collection = "users")
    private Users teacher;
    private SessionType sessionType;
    private RequestStatus status;
    @DocumentReference(lazy = true, collection = "rooms")
    private Room preferredRoom;
    private LocalTime preferredStartTime;
    private LocalTime preferredEndTime;
    private DayOfWeek preferredDay;
    @DocumentReference(lazy = true, collection = "timetables")
    private Timetable timetable;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    @DocumentReference(lazy = true, collection = "users")
    private Users processedBy;
    private String adminComment;

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }

    public ReservationresponseDTO toDTO() {
        return ReservationresponseDTO.fromReservation(this);
    }
}
