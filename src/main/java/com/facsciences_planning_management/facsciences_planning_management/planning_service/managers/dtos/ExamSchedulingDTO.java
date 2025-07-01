package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.ExamTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDate;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidTime;

public record ExamSchedulingDTO(
		String id,
		String roomId,
		String roomCode,
		String ueId,
		String ueCode,
		String timetableId,
		String timeSlotLabel,
		@ValidTime String startTime,
		@ValidTime String endTime,
		String userId,
		String proctorName,
		@ValidDate String date) implements SchedulingDTO {
	public static ExamSchedulingDTO fromExamScheduling(ExamScheduling entity) {
		return new ExamSchedulingDTO(
				entity.getId(),
				entity.getRoom().getId(),
				entity.getRoom().getCode(),
				entity.getUe().getId(),
				entity.getUe().getCode(),
				entity.getTimetable().getId(),
				entity.getTimeSlot().name(),
				entity.getTimeSlot().getStartTime().toString(),
				entity.getTimeSlot().getEndTime().toString(),
				entity.getProctor().getId(),
				entity.getProctor().getFirstName() + " " + entity.getProctor().getLastName(),
				entity.getSessionDate().toString());
	}

	public static ExamSchedulingDTO fromReservation(Reservation reservation) {
		return new ExamSchedulingDTO(
				null,
				reservation.getRoom().getId(),
				null,
				reservation.getUe().getId(),
				null,
				reservation.getTimetable().getId(),
				ExamTimeSlot.get(reservation.getStartTime(), reservation.getEndTime()).name(),
				reservation.getStartTime().toString(),
				reservation.getEndTime().toString(),
				reservation.getTeacher().getId(),
				null,
				reservation.getDate().toString());
	}
}