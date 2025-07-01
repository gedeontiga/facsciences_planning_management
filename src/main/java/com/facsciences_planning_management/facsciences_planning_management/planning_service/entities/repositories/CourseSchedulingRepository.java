package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;

import java.time.DayOfWeek;
import java.util.List;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;

@Repository
public interface CourseSchedulingRepository extends SchedulingRepository<CourseScheduling, String>,
		CourseSchedulingRepositoryCustom {

	List<CourseScheduling> findByTimetableUsedTrueAndTimeSlot(CourseTimeSlot timeSlot);

	List<CourseScheduling> findByAssignedCourseTeacherIdAndAssignedCourseObsoleteFalse(String teacherId);

	boolean existsByRoomAndDayAndTimeSlot(Room room, DayOfWeek day, CourseTimeSlot timeSlot);

	// New methods for conflict detection
	List<CourseScheduling> findByRoomIdAndDayAndTimeSlotAndTimetableUsedTrue(
			String roomId, DayOfWeek day, CourseTimeSlot timeSlot);

	List<CourseScheduling> findByAssignedCourseTeacherIdAndDayAndTimeSlotAndTimetableUsedTrue(
			String teacherId, DayOfWeek day, CourseTimeSlot timeSlot);

	List<CourseScheduling> findByDayAndTimeSlotAndTimetableUsedTrue(DayOfWeek day, CourseTimeSlot timeSlot);

	List<CourseScheduling> findByRoomIdAndDayAndTimetableUsedTrue(String roomId, DayOfWeek day);

	List<CourseScheduling> findByRoomIdAndTimeSlotAndTimetableUsedTrue(String roomId, CourseTimeSlot timeSlot);

	boolean existsByAssignedCourseAndDay(Course course, DayOfWeek day);

	boolean existsByAssignedCourseTeacherAndDayAndTimeSlot(Users user, DayOfWeek day, CourseTimeSlot courseTimeSlot);
}

// Updated CourseSchedulingRepositoryCustom
interface CourseSchedulingRepositoryCustom {
	// List<CourseScheduling>
	// findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelId(String levelId);

	List<CourseScheduling> findByTimetableUsedTrue();

	Page<CourseScheduling> findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelBranchId(
			String branchId, Pageable page);

	// New methods for conflict detection
	List<CourseScheduling> findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelIdAndDayAndTimeSlot(
			String levelId, DayOfWeek day, CourseTimeSlot timeSlot);

	List<CourseScheduling> findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelIdAndDay(
			String levelId, DayOfWeek day);

	List<CourseScheduling> findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelIdAndTimeSlot(
			String levelId, CourseTimeSlot timeSlot);
}