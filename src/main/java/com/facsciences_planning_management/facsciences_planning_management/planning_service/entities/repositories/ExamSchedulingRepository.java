package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import java.time.LocalDate;
import java.util.List;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.ExamTimeSlot;

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