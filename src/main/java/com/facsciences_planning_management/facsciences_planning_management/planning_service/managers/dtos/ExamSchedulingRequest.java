package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.types.HeadCountLabel;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.ValidTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.ValidTimeSlot.TimeSlotType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDate;

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
