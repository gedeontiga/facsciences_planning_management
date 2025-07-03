package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalDateTime;
import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

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
		String teacherName = Optional.ofNullable(reservation.getTeacher()).map(t -> t.getFirstName())
				.orElse(null) + " "
				+ Optional.ofNullable(reservation.getTeacher()).map(t -> t.getLastName()).orElse(null);

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
				Optional.ofNullable(reservation.getTeacher()).map(t -> t.getId()).orElse(null),
				teacherName,
				reservation.getSessionType(),
				reservation.getStatus(),
				Optional.ofNullable(reservation.getRoom()).map(r -> r.getId()).orElse(null),
				Optional.ofNullable(reservation.getRoom()).map(r -> r.getCode()).orElse(null),
				Optional.ofNullable(reservation.getUe()).map(ue -> ue.getId()).orElse(null),
				Optional.ofNullable(reservation.getUe()).map(ue -> ue.getCode()).orElse(null),
				reservation.getStartTime().toString(),
				reservation.getEndTime().toString(),
				reservation.getDate().getDayOfWeek().name(),
				processedById,
				adminName,
				Optional.ofNullable(reservation.getCreatedAt()).map(e -> e.toString()).orElse(null),
				processedAt,
				reservation.getAdminComment());
	}
}