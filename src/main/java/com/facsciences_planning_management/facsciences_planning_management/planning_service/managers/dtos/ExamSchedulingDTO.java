package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.types.SessionType;

public record ExamSchedulingDTO(
		String id,
		String roomId,
		String ueId,
		String planningId,
		LocalTime startTime,
		LocalTime endTime,
		SessionType sessionType,
		String proctorId,
		LocalDateTime sessionDate) implements SchedulingDTO {
	public static ExamSchedulingDTO fromEntity(ExamScheduling entity) {
		return new ExamSchedulingDTO(
				entity.getId(),
				entity.getRoom().getId(),
				entity.getUe().getId(),
				entity.getPlanning().getId(),
				entity.getStartTime(),
				entity.getEndTime(),
				entity.getSessionType(),
				entity.getProctor().getId(),
				entity.getSessionDate());
	}
}