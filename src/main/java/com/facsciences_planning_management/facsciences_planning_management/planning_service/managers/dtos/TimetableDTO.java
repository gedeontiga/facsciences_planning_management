package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Set;
import java.util.stream.Collectors;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Scheduling;

import lombok.Singular;

public record TimetableDTO(
		String id,
		@Singular Set<SchedulingDTO> schedules,
		Year academicYear,
		String semester,
		LocalDateTime createdAt) {
	public static TimetableDTO fromTimetable(final Timetable entity) {
		return new TimetableDTO(entity.getId(),
				entity.getSchedules().stream()
						.map(Scheduling::toDTO)
						.collect(Collectors.toSet()),
				entity.getAcademicYear(),
				entity.getSemester(),
				entity.getCreatedAt());
	}
}