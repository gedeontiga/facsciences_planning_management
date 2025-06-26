package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Scheduling;

import lombok.Singular;

public record TimetableDTO(
		String id,
		String name,
		String description,
		String levelId,
		String levelCode,
		String sessionType,
		LocalDate startDate,
		@Singular Set<SchedulingDTO> schedules,
		String academicYear,
		String semester,
		LocalDateTime createdAt) {
	public static TimetableDTO fromTimetable(final Timetable entity) {
		return new TimetableDTO(entity.getId(),
				entity.getName(),
				entity.getDescription(),
				entity.getLevel().getId(),
				entity.getLevel().getCode(),
				entity.getSessionType().name(),
				entity.getStartDate(),
				entity.getSchedules().stream()
						.map(Scheduling::toDTO)
						.collect(Collectors.toSet()),
				entity.getAcademicYear(),
				entity.getSemester(),
				entity.getCreatedAt());
	}
}