package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;

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
		String preferredStartTime,
		String preferredEndTime,
		DayOfWeek preferredDay,
		String processedById,
		String adminName,
		String createdAt,
		String processedAt,
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
				reservation.getPreferredStartTime().toString(),
				reservation.getPreferredEndTime().toString(),
				reservation.getPreferredDay(),
				reservation.getProcessedBy().getId(),
				adminName,
				reservation.getCreatedAt().toString(),
				reservation.getProcessedAt().toString(),
				reservation.getAdminComment());
	}
}
