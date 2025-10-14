package com.facsciencesuy1.planning_management.planning_service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.ExamScheduling;
import com.facsciencesuy1.planning_management.entities.types.TimeSlot.ExamTimeSlot;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamSchedulingRepository extends SchedulingRepository<ExamScheduling, String> {

	boolean existsByRoomIdAndSessionDateAndTimeSlot(String roomId, LocalDate date, ExamTimeSlot examTimeSlot);

	boolean existsByProctorIdAndSessionDateAndTimeSlot(String userId, LocalDate date, ExamTimeSlot examTimeSlot);

	Page<ExamScheduling> findByActiveIsTrueAndBranchId(String branchId, Pageable page);

	List<ExamScheduling> findByActiveIsTrueAndLevelId(String levelId);

	List<ExamScheduling> findByLevelIdAndSessionDateAndTimeSlotAndActiveIsTrue(
			String levelId, LocalDate sessionDate, ExamTimeSlot timeSlot);

	List<ExamScheduling> findByLevelIdAndSessionDateAndActiveIsTrue(
			String levelId, LocalDate sessionDate);
}

// interface ExamSchedulingRepositoryCustom {
// }