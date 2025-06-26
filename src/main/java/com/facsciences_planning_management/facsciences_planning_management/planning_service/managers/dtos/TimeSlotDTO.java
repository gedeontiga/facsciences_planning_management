package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.ExamTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;

public record TimeSlotDTO(
		String startTime,
		String endTime,
		Long duration,
		String label) {

	public static TimeSlotDTO fromTimeSlot(CourseTimeSlot timeSlot) {
		return new TimeSlotDTO(timeSlot.getStartTime().toString(), timeSlot.getEndTime().toString(),
				timeSlot.getDuration().toHours(),
				timeSlot.name());
	}

	public static TimeSlotDTO fromTimeSlot(ExamTimeSlot timeSlot) {
		return new TimeSlotDTO(timeSlot.getStartTime().toString(), timeSlot.getEndTime().toString(),
				timeSlot.getDuration().toHours(),
				timeSlot.name());
	}
}
