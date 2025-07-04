package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reservations")
public class Reservation {
    @Id
    private String id;
    @DocumentReference(lazy = true, collection = "users")
    private Users teacher;
    private SessionType sessionType;
    @DocumentReference(lazy = true, collection = "ues")
    private Ue ue;
    private RequestStatus status;
    @DocumentReference(lazy = true, collection = "rooms")
    private Room room;
    private String timeSlotLabel; // e.g., "COURSE_1", "EXAM_2"
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private Long headCount;
    private String timetableId;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime processedAt;
    @DocumentReference(lazy = true, collection = "users")
    private Users processedBy;
    private String adminComment;
    @DocumentReference(lazy = true, collection = "schedulings")
    private Scheduling scheduling;

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }

    public ReservationResponseDTO toDTO() {
        return ReservationResponseDTO.fromReservation(this);
    }
}
