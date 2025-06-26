package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import java.util.List;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;

@Repository
public interface CourseSchedulingRepository extends
		SchedulingRepository<CourseScheduling, String>,
		CourseSchedulingRepositoryCustom {

	List<CourseScheduling> findByTimetableUsedTrueAndTimeSlot(CourseTimeSlot timeSlot);

	List<CourseScheduling> findByAssignedCourseTeacherIdAndAssignedCourseObsoleteFalse(
			String teacherId);
}

interface CourseSchedulingRepositoryCustom {
	List<CourseScheduling> findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelId(String levelId);

	Page<CourseScheduling> findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelBranchId(
			String branchId,
			Pageable page);
}