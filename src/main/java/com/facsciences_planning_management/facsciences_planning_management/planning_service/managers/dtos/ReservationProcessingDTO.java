package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;

public record ReservationProcessingDTO(
        RequestStatus status,
        String message) {
}
