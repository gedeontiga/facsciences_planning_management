package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;

public record CourseSchedulingDTO(
		String id,
		String roomId,
		String roomCode,
		String ueId,
		String ueCode,
		String timetableId,
		String timeSlotLabel,
		LocalTime startTime,
		LocalTime endTime,
		String teacherId,
		String teacherName,
		DayOfWeek day) implements SchedulingDTO {
	public static CourseSchedulingDTO fromEntity(CourseScheduling entity) {
		return new CourseSchedulingDTO(
				entity.getId(),
				entity.getRoom().getId(),
				entity.getRoom().getCode(),
				entity.getAssignedCourse().getUe().getId(),
				entity.getAssignedCourse().getUe().getName(),
				entity.getTimetable().getId(),
				entity.getTimeSlot().name(),
				entity.getTimeSlot().getStartTime(),
				entity.getTimeSlot().getEndTime(),
				entity.getAssignedCourse().getTeacher().getId(),
				entity.getAssignedCourse().getTeacher().getFirstName() + " "
						+ entity.getAssignedCourse().getTeacher().getLastName(),
				entity.getDay());
	}
}