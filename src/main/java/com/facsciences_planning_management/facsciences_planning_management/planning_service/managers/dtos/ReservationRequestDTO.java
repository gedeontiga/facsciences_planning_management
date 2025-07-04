package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.ValidTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.ValidTimeSlot.TimeSlotType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@NotNull
public record ReservationRequestDTO(
		String teacherId,
		@Valid SessionType sessionType,
		String ueId,
		String roomId,
		@NotNull @ValidTimeSlot(TimeSlotType.ANY) String timeSlotLabel,
		@NotNull @ValidDate String date,
		Long headCount,
		String timetableId) {

	public static ReservationRequestDTO fromReservation(Reservation reservation) {
		return new ReservationRequestDTO(
				Optional.ofNullable(reservation.getTeacher()).map(t -> t.getId()).orElse(null),
				reservation.getSessionType(),
				Optional.ofNullable(reservation.getRoom()).map(r -> r.getId()).orElse(null),
				Optional.ofNullable(reservation.getUe()).map(ue -> ue.getId()).orElse(null),
				reservation.getTimeSlotLabel(),
				reservation.getDate() != null ? reservation.getDate().toString() : null,
				reservation.getHeadCount(),
				reservation.getTimetableId());
	}
}
