package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalDate;
import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.types.HeadCountLabel;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.ValidTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.ValidTimeSlot.TimeSlotType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDayOfWeek;

import jakarta.validation.constraints.NotNull;

public record CourseSchedulingRequest(
		String id,
		@NotNull String roomId,
		@NotNull String ueId,
		@NotNull String userId,
		@NotNull String timetableId,
		@NotNull @ValidTimeSlot(TimeSlotType.ANY) String timeSlotLabel,
		@NotNull @ValidDayOfWeek String day,
		@NotNull Long headCount,
		HeadCountLabel headCountLabel) implements SchedulingRequest {

	public static CourseSchedulingRequest fromReservation(ReservationRequestDTO reservation) {
		return new CourseSchedulingRequest(
				null,
				reservation.roomId(),
				reservation.ueId(),
				reservation.teacherId(),
				reservation.timetableId(),
				reservation.timeSlotLabel(),
				Optional.ofNullable(reservation.date())
						.map(date -> LocalDate.parse(date).getDayOfWeek().name())
						.orElse(null),
				reservation.headCount(),
				null);
	}
}
