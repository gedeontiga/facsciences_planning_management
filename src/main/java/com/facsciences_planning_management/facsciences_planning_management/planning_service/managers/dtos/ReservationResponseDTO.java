package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

public record ReservationResponseDTO(
		String id,
		String teacherId,
		String teacherName,
		SessionType sessionType,
		RequestStatus status,
		String preferredRoomId,
		String roomCode,
		LocalTime preferredStartTime,
		LocalTime preferredEndTime,
		DayOfWeek preferredDay,
		String processedById,
		String adminName,
		LocalDateTime createdAt,
		LocalDateTime processedAt,
		String adminComment) {

	public static ReservationResponseDTO fromReservation(Reservation reservation) {
		String teacherName = reservation.getTeacher().getFirstName() + " "
				+ reservation.getTeacher().getLastName();
		String adminName = reservation.getProcessedBy().getFirstName() + " "
				+ reservation.getProcessedBy().getLastName();
		return new ReservationResponseDTO(
				reservation.getId(),
				reservation.getTeacher().getId(),
				teacherName,
				reservation.getSessionType(),
				reservation.getStatus(),
				reservation.getPreferredRoom().getId(),
				reservation.getPreferredRoom().getCode(),
				reservation.getPreferredStartTime(),
				reservation.getPreferredEndTime(),
				reservation.getPreferredDay(),
				reservation.getProcessedBy().getId(),
				adminName,
				reservation.getCreatedAt(),
				reservation.getProcessedAt(),
				reservation.getAdminComment());
	}
}
