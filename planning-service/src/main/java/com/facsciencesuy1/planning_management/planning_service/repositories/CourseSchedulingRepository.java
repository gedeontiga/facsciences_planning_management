package com.facsciencesuy1.planning_management.planning_service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.CourseScheduling;
import com.facsciencesuy1.planning_management.entities.types.TimeSlot.CourseTimeSlot;

import java.time.DayOfWeek;
import java.util.List;

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