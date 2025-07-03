package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

import jakarta.validation.Valid;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Scheduling;

import lombok.Singular;

public record TimetableDTO(
		String id,
		String name,
		String description,
		String levelId,
		String levelCode,
		SessionType sessionType,
		@Singular Set<SchedulingDTO> schedules,
		String academicYear,
		@Valid Semester semester,
		String createdAt) {
	public static TimetableDTO fromTimetable(final Timetable entity) {
		return new TimetableDTO(entity.getId(),
				entity.getName(),
				entity.getDescription(),
				Optional.ofNullable(entity.getLevel()).map(l -> l.getId()).orElse(null),
				Optional.ofNullable(entity.getLevel()).map(l -> l.getCode()).orElse(null),
				entity.getSessionType(),
				entity.getSchedules().stream()
						.map(Scheduling::toDTO)
						.collect(Collectors.toSet()),
				entity.getAcademicYear(),
				entity.getSemester(),
				Optional.ofNullable(entity.getCreatedAt()).map(e -> e.toString()).orElse(null));
	}
}