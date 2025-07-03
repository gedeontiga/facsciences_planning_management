package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.ExamTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.types.HeadCountLabel;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDate;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidTime;

import jakarta.annotation.Nonnull;

public record ExamSchedulingDTO(
		String id,
		String roomId,
		String roomCode,
		String ueId,
		String ueCode,
		String timetableId,
		String timeSlotLabel,
		@ValidTime String startTime,
		@ValidTime String endTime, String userId,
		String proctorName,
		@ValidDate String date,
		@Nonnull Long headCount,
		HeadCountLabel headCountLabel) implements SchedulingDTO {

	public static ExamSchedulingDTO fromExamScheduling(ExamScheduling entity) {
		String teacherName = Optional.ofNullable(entity
				.getProctor()).map(p -> p.getFirstName())
				.orElse(null) + " "
				+ Optional.ofNullable(entity.getProctor()).map(p -> p.getLastName()).orElse(null);
		HeadCountLabel headCountLabel = Optional.ofNullable(entity.getHeadCountLabel())
				.map(hcl -> HeadCountLabel.valueOf(hcl)).orElse(null);
		return new ExamSchedulingDTO(
				entity.getId(),
				Optional.ofNullable(entity.getRoom()).map(r -> r.getId()).orElse(null),
				Optional.ofNullable(entity.getRoom()).map(r -> r.getCode()).orElse(null),
				Optional.ofNullable(entity.getUe()).map(ue -> ue.getId()).orElse(null),
				Optional.ofNullable(entity.getUe()).map(ue -> ue.getCode()).orElse(null),
				Optional.ofNullable(entity.getTimetable()).map(t -> t.getId()).orElse(null),
				entity.getTimeSlot().name(),
				entity.getTimeSlot().getStartTime().toString(),
				entity.getTimeSlot().getEndTime().toString(),
				Optional.ofNullable(entity.getProctor()).map(p -> p.getId()).orElse(null),
				teacherName,
				Optional.ofNullable(entity.getSessionDate()).map(e -> e.toString()).orElse(null),
				entity.getHeadCount(),
				headCountLabel);
	}

	public static ExamSchedulingDTO fromReservation(Reservation reservation) {
		return new ExamSchedulingDTO(
				null,
				reservation.getRoom().getId(),
				null,
				reservation.getUe().getId(),
				null,
				reservation.getTimetableId(),
				ExamTimeSlot.fromTimeSlot(reservation.getStartTime(), reservation.getEndTime()).name(),
				reservation.getStartTime().toString(),
				reservation.getEndTime().toString(),
				reservation.getTeacher().getId(),
				null,
				Optional.ofNullable(reservation.getDate()).map(e -> e.toString()).orElse(
						null),
				reservation.getHeadCount(),
				null);
	}
}