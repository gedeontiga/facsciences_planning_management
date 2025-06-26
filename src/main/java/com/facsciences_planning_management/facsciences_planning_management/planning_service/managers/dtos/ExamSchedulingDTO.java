package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;

public record ExamSchedulingDTO(
		String id,
		String roomId,
		String roomCode,
		String ueId,
		String ueCode,
		String timetableId,
		String timeSlotLabel,
		LocalTime startTime,
		LocalTime endTime,
		String proctorId,
		String proctorName,
		LocalDateTime sessionDate) implements SchedulingDTO {
	public static ExamSchedulingDTO fromExamScheduling(ExamScheduling entity) {
		return new ExamSchedulingDTO(
				entity.getId(),
				entity.getRoom().getId(),
				entity.getRoom().getCode(),
				entity.getUe().getId(),
				entity.getUe().getCode(),
				entity.getTimetable().getId(),
				entity.getTimeSlot().name(),
				entity.getTimeSlot().getStartTime(),
				entity.getTimeSlot().getEndTime(),
				entity.getProctor().getId(),
				entity.getProctor().getFirstName() + " " + entity.getProctor().getLastName(),
				entity.getSessionDate());
	}
}