package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalDateTime;
import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.components.annotations.SafeMapping;
import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

@SafeMapping
public record ReservationResponseDTO(
		String id,
		String teacherId,
		String teacherName,
		SessionType sessionType,
		RequestStatus status,
		String roomId,
		String roomCode,
		String ueId,
		String ueCode,
		String startTime,
		String endTime,
		String preferredDay,
		String processedById,
		String adminName,
		String createdAt,
		String processedAt,
		String adminComment) {
	public static ReservationResponseDTO fromReservation(Reservation reservation) {
		String teacherName = reservation.getTeacher().getFirstName() + " " + reservation.getTeacher().getLastName();

		String processedById = Optional.ofNullable(reservation.getProcessedBy())
				.map(Users::getId)
				.orElse(null);

		String adminName = Optional.ofNullable(reservation.getProcessedBy())
				.map(admin -> admin.getFirstName() + " " + admin.getLastName())
				.orElse(null);

		String processedAt = Optional.ofNullable(reservation.getProcessedAt())
				.map(LocalDateTime::toString)
				.orElse(null);

		return new ReservationResponseDTO(
				reservation.getId(),
				reservation.getTeacher().getId(),
				teacherName,
				reservation.getSessionType(),
				reservation.getStatus(),
				reservation.getRoom().getId(),
				reservation.getRoom().getCode(),
				reservation.getUe().getId(),
				reservation.getUe().getCode(),
				reservation.getStartTime().toString(),
				reservation.getEndTime().toString(),
				reservation.getDate().getDayOfWeek().name(),
				processedById,
				adminName,
				reservation.getCreatedAt().toString(),
				processedAt,
				reservation.getAdminComment());
	}
}