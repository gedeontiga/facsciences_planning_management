package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.components.annotations.SafeMapping;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDayOfWeek;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidTime;

@SafeMapping
public record CourseSchedulingDTO(
		String id,
		String roomId,
		String roomCode,
		String ueId,
		String ueCode,
		String timetableId,
		String timeSlotLabel,
		@ValidTime String startTime,
		@ValidTime String endTime,
		String userId,
		String teacherName,
		@ValidDayOfWeek String day) implements SchedulingDTO {
	public static CourseSchedulingDTO fromEntity(CourseScheduling entity) {
		return new CourseSchedulingDTO(
				entity.getId(),
				entity.getRoom().getId(),
				entity.getRoom().getCode(),
				entity.getAssignedCourse().getUe().getId(),
				entity.getAssignedCourse().getUe().getCode(),
				entity.getTimetable().getId(),
				entity.getTimeSlot().name(),
				entity.getTimeSlot().getStartTime().toString(),
				entity.getTimeSlot().getEndTime().toString(),
				entity.getAssignedCourse().getTeacher().getId(),
				entity.getAssignedCourse().getTeacher().getFirstName() + " "
						+ entity.getAssignedCourse().getTeacher().getLastName(),
				entity.getDay().toString());
	}

	public static CourseSchedulingDTO fromReservation(Reservation reservation) {
		return new CourseSchedulingDTO(
				null,
				reservation.getRoom().getId(),
				null,
				reservation.getUe().getId(),
				null,
				reservation.getTimetableId(),
				CourseTimeSlot.get(reservation.getStartTime(), reservation.getEndTime()).name(),
				reservation.getStartTime().toString(),
				reservation.getEndTime().toString(),
				reservation.getTeacher().getId(),
				null,
				reservation.getDate().getDayOfWeek().name());
	}
}