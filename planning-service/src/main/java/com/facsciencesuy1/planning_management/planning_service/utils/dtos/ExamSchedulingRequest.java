package com.facsciencesuy1.planning_management.planning_service.utils.dtos;

import com.facsciencesuy1.planning_management.dtos.types.HeadCountLabel;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.ValidTimeSlot;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.ValidTimeSlot.TimeSlotType;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces.ValidDate;

public record ExamSchedulingRequest(
		String roomId,
		String ueId,
		String userId,
		String timetableId,
		@ValidTimeSlot(TimeSlotType.ANY) String timeSlotLabel,
		@ValidDate String date,
		Long headCount,
		HeadCountLabel headCountLabel) implements SchedulingRequest {

	public static ExamSchedulingRequest fromReservation(ReservationRequestDTO reservation) {
		return new ExamSchedulingRequest(
				reservation.roomId(),
				reservation.ueId(),
				reservation.teacherId(),
				reservation.timetableId(),
				reservation.timeSlotLabel(),
				reservation.date(),
				reservation.headCount(),
				null);
	}
}
