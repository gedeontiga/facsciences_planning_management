package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.types.HeadCountLabel;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.ValidTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.ValidTimeSlot.TimeSlotType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDate;

import jakarta.validation.constraints.NotNull;

public record ExamSchedulingRequest(
		String id,
		@NotNull String roomId,
		@NotNull String ueId,
		@NotNull String userId,
		@NotNull String timetableId,
		@NotNull @ValidTimeSlot(TimeSlotType.ANY) String timeSlotLabel,
		@NotNull @ValidDate String date,
		@NotNull Long headCount,
		HeadCountLabel headCountLabel) implements SchedulingRequest {

	public static ExamSchedulingRequest fromReservation(ReservationRequestDTO reservation) {
		return new ExamSchedulingRequest(
				null,
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
