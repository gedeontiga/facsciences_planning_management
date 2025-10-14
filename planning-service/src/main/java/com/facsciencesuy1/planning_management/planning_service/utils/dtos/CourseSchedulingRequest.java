package com.facsciencesuy1.planning_management.planning_service.utils.dtos;

import java.time.LocalDate;
import java.util.Optional;

import com.facsciencesuy1.planning_management.dtos.types.HeadCountLabel;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.ValidTimeSlot;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.ValidTimeSlot.TimeSlotType;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces.ValidDayOfWeek;

public record CourseSchedulingRequest(
		String roomId,
		String ueId,
		String userId,
		String timetableId,
		@ValidTimeSlot(TimeSlotType.ANY) String timeSlotLabel,
		@ValidDayOfWeek String day,
		Long headCount,
		HeadCountLabel headCountLabel) implements SchedulingRequest {

	public static CourseSchedulingRequest fromReservation(ReservationRequestDTO reservation) {
		return new CourseSchedulingRequest(
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
