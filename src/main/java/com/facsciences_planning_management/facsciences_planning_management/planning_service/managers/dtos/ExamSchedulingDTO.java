package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDate;

public record ExamSchedulingDTO(
		String id,
		String roomId,
		String roomCode,
		String ueId,
		String ueCode,
		String timetableId,
		String timeSlotLabel,
		String startTime,
		String endTime,
		String proctorId,
		String proctorName,
		@ValidDate String sessionDate) implements SchedulingDTO {
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
}