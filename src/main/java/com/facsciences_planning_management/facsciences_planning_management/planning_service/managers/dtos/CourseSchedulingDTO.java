package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDayOfWeek;

public record CourseSchedulingDTO(
		String id,
		String roomId,
		String roomCode,
		String ueId,
		String ueCode,
		String timetableId,
		String timeSlotLabel,
		String startTime,
		String endTime,
		String teacherId,
		String teacherName,
		@ValidDayOfWeek String day) implements SchedulingDTO {
	public static CourseSchedulingDTO fromEntity(CourseScheduling entity) {
		return new CourseSchedulingDTO(
				entity.getId(),
				entity.getRoom().getId(),
				entity.getRoom().getCode(),
				entity.getAssignedCourse().getUe().getId(),
				entity.getAssignedCourse().getUe().getName(),
				entity.getTimetable().getId(),
				entity.getTimeSlot().name(),
				entity.getTimeSlot().getStartTime().toString(),
				entity.getTimeSlot().getEndTime().toString(),
				entity.getAssignedCourse().getTeacher().getId(),
				entity.getAssignedCourse().getTeacher().getFirstName() + " "
						+ entity.getAssignedCourse().getTeacher().getLastName(),
				entity.getDay().toString());
	}
}