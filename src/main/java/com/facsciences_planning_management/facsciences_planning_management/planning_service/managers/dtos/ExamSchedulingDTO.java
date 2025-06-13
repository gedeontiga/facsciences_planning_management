package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

public record ExamSchedulingDTO(
		String id,
		String roomId,
		String ueId,
		String timetableId,
		LocalTime startTime,
		LocalTime endTime,
		SessionType sessionType,
		String proctorId,
		LocalDateTime sessionDate) implements SchedulingDTO {
	public static ExamSchedulingDTO fromExamScheduling(ExamScheduling entity) {
		return new ExamSchedulingDTO(
				entity.getId(),
				entity.getRoom().getId(),
				entity.getUe().getId(),
				entity.getTimetable().getId(),
				entity.getStartTime(),
				entity.getEndTime(),
				entity.getSessionType(),
				entity.getProctor().getId(),
				entity.getSessionDate());
	}
}