package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;

import java.time.LocalDate;
import java.util.List;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.ExamTimeSlot;

@Repository
public interface ExamSchedulingRepository extends SchedulingRepository<ExamScheduling, String>,
		ExamSchedulingRepositoryCustom {

	List<ExamScheduling> findByTimetableUsedTrueAndTimeSlot(ExamTimeSlot timeSlot);

	List<ExamScheduling> findByTimetableUsedTrueAndProctorId(String proctorId);

	// New methods for conflict detection
	List<ExamScheduling> findByRoomIdAndSessionDateAndTimeSlotAndTimetableUsedTrue(
			String roomId, LocalDate sessionDate, ExamTimeSlot timeSlot);

	List<ExamScheduling> findByProctorIdAndSessionDateAndTimeSlotAndTimetableUsedTrue(
			String proctorId, LocalDate sessionDate, ExamTimeSlot timeSlot);

	List<ExamScheduling> findBySessionDateAndTimeSlotAndTimetableUsedTrue(
			LocalDate sessionDate, ExamTimeSlot timeSlot);

	List<ExamScheduling> findByRoomIdAndSessionDateAndTimetableUsedTrue(
			String roomId, LocalDate sessionDate);

	boolean existsByRoomAndSessionDateAndTimeSlot(Room room, LocalDate date, ExamTimeSlot examTimeSlot);

	boolean existsByProctorAndSessionDateAndTimeSlot(Users user, LocalDate date, ExamTimeSlot examTimeSlot);
}

// Updated ExamSchedulingRepositoryCustom
interface ExamSchedulingRepositoryCustom {
	Page<ExamScheduling> findByTimetableUsedTrueAndUeLevelBranchId(String branchId, Pageable page);

	List<ExamScheduling> findByTimetableUsedTrueAndUeLevelId(String levelId);

	// New methods for conflict detection
	List<ExamScheduling> findByUeLevelIdAndSessionDateAndTimeSlotAndTimetableUsedTrue(
			String levelId, LocalDate sessionDate, ExamTimeSlot timeSlot);

	List<ExamScheduling> findByUeLevelIdAndSessionDateAndTimetableUsedTrue(
			String levelId, LocalDate sessionDate);
}