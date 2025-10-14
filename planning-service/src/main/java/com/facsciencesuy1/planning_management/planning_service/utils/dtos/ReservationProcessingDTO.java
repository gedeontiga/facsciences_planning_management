package com.facsciencesuy1.planning_management.planning_service.utils.dtos;

import com.facsciencesuy1.planning_management.entities.Reservation.RequestStatus;

public record ReservationProcessingDTO(
		String id,
		RequestStatus status,
		String message) {
}
