package com.facsciencesuy1.planning_management.planning_service.utils.dtos;

import com.facsciencesuy1.planning_management.entities.types.TimeSlot.CourseTimeSlot;
import com.facsciencesuy1.planning_management.entities.types.TimeSlot.ExamTimeSlot;

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
