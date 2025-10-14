package com.facsciencesuy1.planning_management.dtos;

import java.util.Optional;

import com.facsciencesuy1.planning_management.dtos.types.HeadCountLabel;
import com.facsciencesuy1.planning_management.entities.ExamScheduling;

public record ExamSchedulingDTO(String id, String roomId, String roomCode, String ueId, String ueCode,
		String timetableId, String timeSlotLabel, String startTime, String endTime, String userId, String proctorName,
		String date, Long headCount, HeadCountLabel headCountLabel) implements SchedulingDTO {

	public static ExamSchedulingDTO fromExamScheduling(ExamScheduling entity) {
		String teacherName = Optional.ofNullable(entity.getProctor()).map(p -> p.getFirstName()).orElse(null) + " "
				+ Optional.ofNullable(entity.getProctor()).map(p -> p.getLastName()).orElse(null);
		HeadCountLabel headCountLabel = Optional.ofNullable(entity.getHeadCountLabel())
				.map(hcl -> HeadCountLabel.valueOf(hcl)).orElse(null);
		return new ExamSchedulingDTO(entity.getId(),
				Optional.ofNullable(entity.getRoom()).map(r -> r.getId()).orElse(null),
				Optional.ofNullable(entity.getRoom()).map(r -> r.getCode()).orElse(null),
				Optional.ofNullable(entity.getUe()).map(ue -> ue.getId()).orElse(null),
				Optional.ofNullable(entity.getUe()).map(ue -> ue.getCode()).orElse(null),
				Optional.ofNullable(entity.getTimetable()).map(t -> t.getId()).orElse(null),
				Optional.ofNullable(entity.getTimeSlot()).map(tm -> tm.name()).orElse(null),
				Optional.ofNullable(entity.getTimeSlot()).map(tm -> tm.getStartTime().toString()).orElse(null),
				Optional.ofNullable(entity.getTimeSlot()).map(tm -> tm.getEndTime().toString()).orElse(null),
				Optional.ofNullable(entity.getProctor()).map(p -> p.getId()).orElse(null), teacherName,
				Optional.ofNullable(entity.getSessionDate()).map(e -> e.toString()).orElse(null), entity.getHeadCount(),
				headCountLabel);
	}
}