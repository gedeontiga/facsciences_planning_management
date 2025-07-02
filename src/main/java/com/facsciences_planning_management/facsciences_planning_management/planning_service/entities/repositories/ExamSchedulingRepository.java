package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import java.time.LocalDate;
import java.util.List;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.ExamTimeSlot;

@Repository
public interface ExamSchedulingRepository extends SchedulingRepository<ExamScheduling, String>,
		ExamSchedulingRepositoryCustom {

	List<ExamScheduling> findByTimetableUsedTrueAndTimeSlot(ExamTimeSlot timeSlot);

	List<ExamScheduling> findByTimetableUsedTrueAndProctorId(String proctorId);

	List<ExamScheduling> findByRoomIdAndSessionDateAndTimeSlotAndTimetableUsedTrue(
			String roomId, LocalDate sessionDate, ExamTimeSlot timeSlot);

	List<ExamScheduling> findByProctorIdAndSessionDateAndTimeSlotAndTimetableUsedTrue(
			String proctorId, LocalDate sessionDate, ExamTimeSlot timeSlot);

	List<ExamScheduling> findBySessionDateAndTimeSlotAndTimetableUsedTrue(
			LocalDate sessionDate, ExamTimeSlot timeSlot);

	List<ExamScheduling> findByRoomIdAndSessionDateAndTimetableUsedTrue(
			String roomId, LocalDate sessionDate);

	boolean existsByRoomIdAndSessionDateAndTimeSlot(String roomId, LocalDate date, ExamTimeSlot examTimeSlot);

	boolean existsByProctorIdAndSessionDateAndTimeSlot(String userId, LocalDate date, ExamTimeSlot examTimeSlot);
}

interface ExamSchedulingRepositoryCustom {
	Page<ExamScheduling> findByTimetableUsedTrueAndUeLevelBranchId(String branchId, Pageable page);

	List<ExamScheduling> findByTimetableUsedTrueAndUeLevelId(String levelId);

	List<ExamScheduling> findByUeLevelIdAndSessionDateAndTimeSlotAndTimetableUsedTrue(
			String levelId, LocalDate sessionDate, ExamTimeSlot timeSlot);

	List<ExamScheduling> findByUeLevelIdAndSessionDateAndTimetableUsedTrue(
			String levelId, LocalDate sessionDate);
}