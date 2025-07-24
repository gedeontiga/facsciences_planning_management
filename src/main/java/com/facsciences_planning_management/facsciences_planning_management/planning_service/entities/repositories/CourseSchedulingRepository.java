package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import java.time.DayOfWeek;
import java.util.List;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;

@Repository
public interface CourseSchedulingRepository extends SchedulingRepository<CourseScheduling, String> {

	// List<CourseScheduling> findByTimetableUsedTrueAndTimeSlot(CourseTimeSlot
	// timeSlot);

	boolean existsByRoomIdAndDayAndTimeSlot(String roomId, DayOfWeek day, CourseTimeSlot timeSlot);

	List<CourseScheduling> findByRoomIdAndDayAndTimeSlotAndTimetableUsedTrue(
			String roomId, DayOfWeek day, CourseTimeSlot timeSlot);

	List<CourseScheduling> findByDayAndTimeSlotAndTimetableUsedTrue(DayOfWeek day, CourseTimeSlot timeSlot);

	List<CourseScheduling> findByRoomIdAndDayAndTimetableUsedTrue(String roomId, DayOfWeek day);

	List<CourseScheduling> findByRoomIdAndTimeSlotAndTimetableUsedTrue(String roomId, CourseTimeSlot timeSlot);

	boolean existsByAssignedCourseIdAndDay(String courseId, DayOfWeek day);

	boolean existsByTeacherIdAndDayAndTimeSlot(String userId, DayOfWeek day,
			CourseTimeSlot courseTimeSlot);

	List<CourseScheduling> findByHeadCountIsGreaterThanEqualAndActiveIsTrue(Long headCount);

	// List<CourseScheduling> findByActiveIsTrue();

	Page<CourseScheduling> findByActiveIsTrueAndBranchId(
			String branchId, Pageable page);

	List<CourseScheduling> findByActiveIsTrueAndLevelIdAndDayAndTimeSlot(
			String levelId, DayOfWeek day, CourseTimeSlot timeSlot);

	List<CourseScheduling> findByActiveIsTrueAndLevelIdAndDay(
			String levelId, DayOfWeek day);

	List<CourseScheduling> findByActiveIsTrueAndLevelIdAndTimeSlot(
			String levelId, CourseTimeSlot timeSlot);
}

// interface CourseSchedulingRepositoryCustom {
// }