package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.types.HeadCountLabel;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDayOfWeek;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record CourseSchedulingDTO(
		String id,
		String roomId,
		String roomCode,
		String ueId,
		String ueCode,
		String timetableId,
		String timeSlotLabel,
		@ValidTime String startTime,
		@ValidTime String endTime, String userId,
		String teacherName,
		@ValidDayOfWeek String day,
		Long headCount,
		HeadCountLabel headCountLabel) implements SchedulingDTO {

	public static CourseSchedulingDTO fromEntity(CourseScheduling entity) {
		String teacherName = Optional.ofNullable(entity.getAssignedCourse()).map(t -> t.getTeacher().getFirstName())
				.orElse(null) + " "
				+ Optional.ofNullable(entity.getAssignedCourse()).map(t -> t.getTeacher().getLastName()).orElse(null);
		HeadCountLabel headCountLabel = Optional.ofNullable(entity.getHeadCountLabel())
				.map(hcl -> HeadCountLabel.valueOf(hcl)).orElse(null);
		return new CourseSchedulingDTO(
				entity.getId(),
				Optional.ofNullable(entity.getRoom()).map(r -> r.getId()).orElse(null),
				Optional.ofNullable(entity.getRoom()).map(r -> r.getCode()).orElse(null),
				Optional.ofNullable(entity.getAssignedCourse()).map(c -> c.getUe().getId()).orElse(null),
				Optional.ofNullable(entity.getAssignedCourse()).map(c -> c.getUe().getCode()).orElse(null),
				Optional.ofNullable(entity.getTimetable()).map(t -> t.getId()).orElse(null),
				Optional.ofNullable(entity.getTimeSlot()).map(tm -> tm.name()).orElse(null),
				Optional.ofNullable(entity.getTimeSlot()).map(tm -> tm.getStartTime().toString()).orElse(null),
				Optional.ofNullable(entity.getTimeSlot()).map(tm -> tm.getEndTime().toString())
						.orElse(null),
				Optional.ofNullable(entity.getAssignedCourse()).map(t -> t.getTeacher().getId()).orElse(null),
				teacherName,
				Optional.ofNullable(entity.getDay()).map(e -> e.toString()).orElse(null),
				entity.getHeadCount(),
				headCountLabel);
	}
}