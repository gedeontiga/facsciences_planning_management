package com.facsciencesuy1.planning_management.dtos;

import java.util.Optional;

import com.facsciencesuy1.planning_management.dtos.types.HeadCountLabel;
import com.facsciencesuy1.planning_management.entities.CourseScheduling;

public record CourseSchedulingDTO(String id, String roomId, String roomCode, String ueId, String ueCode,
		String timetableId, String timeSlotLabel, String startTime, String endTime, String userId, String teacherName,
		String day, Long headCount, HeadCountLabel headCountLabel) implements SchedulingDTO {

	public static CourseSchedulingDTO fromEntity(CourseScheduling entity) {
		String teacherName = Optional.ofNullable(entity.getAssignedCourse()).map(t -> t.getTeacher().getFirstName())
				.orElse(null) + " "
				+ Optional.ofNullable(entity.getAssignedCourse()).map(t -> t.getTeacher().getLastName()).orElse(null);
		HeadCountLabel headCountLabel = Optional.ofNullable(entity.getHeadCountLabel())
				.map(hcl -> HeadCountLabel.valueOf(hcl)).orElse(null);
		return new CourseSchedulingDTO(entity.getId(),
				Optional.ofNullable(entity.getRoom()).map(r -> r.getId()).orElse(null),
				Optional.ofNullable(entity.getRoom()).map(r -> r.getCode()).orElse(null),
				Optional.ofNullable(entity.getAssignedCourse()).map(c -> c.getUe().getId()).orElse(null),
				Optional.ofNullable(entity.getAssignedCourse()).map(c -> c.getUe().getCode()).orElse(null),
				Optional.ofNullable(entity.getTimetable()).map(t -> t.getId()).orElse(null),
				Optional.ofNullable(entity.getTimeSlot()).map(tm -> tm.name()).orElse(null),
				Optional.ofNullable(entity.getTimeSlot()).map(tm -> tm.getStartTime().toString()).orElse(null),
				Optional.ofNullable(entity.getTimeSlot()).map(tm -> tm.getEndTime().toString()).orElse(null),
				Optional.ofNullable(entity.getAssignedCourse()).map(t -> t.getTeacher().getId()).orElse(null),
				teacherName, Optional.ofNullable(entity.getDay()).map(e -> e.name()).orElse(null),
				entity.getHeadCount(), headCountLabel);
	}
}