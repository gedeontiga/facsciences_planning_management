package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDateTime;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidTime;

import jakarta.validation.Valid;

public record ReservationRequestDTO(
		String teacherId,
		@Valid SessionType sessionType,
		String ueId,
		String roomId,
		@ValidTime String startTime,
		@ValidTime String endTime,
		@ValidDateTime String date,
		String timetableId) {
}
