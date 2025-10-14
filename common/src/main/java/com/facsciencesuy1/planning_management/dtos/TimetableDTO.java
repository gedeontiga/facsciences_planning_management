package com.facsciencesuy1.planning_management.dtos;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.facsciencesuy1.planning_management.entities.Timetable;
import com.facsciencesuy1.planning_management.entities.types.Semester;
import com.facsciencesuy1.planning_management.entities.types.SessionType;

import jakarta.validation.Valid;

import com.facsciencesuy1.planning_management.entities.Scheduling;

import lombok.Singular;

public record TimetableDTO(String id, String name, String description, String levelId, String levelCode,
		SessionType sessionType, @Singular Set<SchedulingDTO> schedules, String academicYear, @Valid Semester semester,
		String createdAt) {
	public static TimetableDTO fromTimetable(final Timetable entity) {
		return new TimetableDTO(entity.getId(), entity.getName(), entity.getDescription(),
				Optional.ofNullable(entity.getLevel()).map(l -> l.getId()).orElse(null),
				Optional.ofNullable(entity.getLevel()).map(l -> l.getCode()).orElse(null), entity.getSessionType(),
				entity.getSchedules().stream().map(Scheduling::toDTO).collect(Collectors.toSet()),
				entity.getAcademicYear(), entity.getSemester(),
				Optional.ofNullable(entity.getCreatedAt()).map(e -> e.toString()).orElse(null));
	}
}