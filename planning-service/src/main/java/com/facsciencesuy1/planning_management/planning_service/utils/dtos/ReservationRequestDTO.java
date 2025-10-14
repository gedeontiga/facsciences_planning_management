package com.facsciencesuy1.planning_management.planning_service.utils.dtos;

import java.util.Optional;

import com.facsciencesuy1.planning_management.entities.Reservation;
import com.facsciencesuy1.planning_management.entities.types.SessionType;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.ValidTimeSlot;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.ValidTimeSlot.TimeSlotType;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces.ValidDate;

import jakarta.validation.Valid;

public record ReservationRequestDTO(
		String teacherId,
		@Valid SessionType sessionType,
		String ueId,
		String roomId,
		@ValidTimeSlot(TimeSlotType.ANY) String timeSlotLabel,
		@ValidDate String date,
		Long headCount,
		String timetableId) {

	public static ReservationRequestDTO fromReservation(Reservation reservation) {
		return new ReservationRequestDTO(
				Optional.ofNullable(reservation.getTeacher()).map(t -> t.getId()).orElse(null),
				reservation.getSessionType(),
				Optional.ofNullable(reservation.getUe()).map(ue -> ue.getId()).orElse(null),
				Optional.ofNullable(reservation.getRoom()).map(r -> r.getId()).orElse(null),
				reservation.getTimeSlotLabel(),
				reservation.getDate() != null ? reservation.getDate().toString() : null,
				reservation.getHeadCount(),
				reservation.getTimetableId());
	}
}
