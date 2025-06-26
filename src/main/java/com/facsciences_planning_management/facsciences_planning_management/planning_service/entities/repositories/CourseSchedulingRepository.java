package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import java.util.List;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;

@Repository
public interface CourseSchedulingRepository extends
                SchedulingRepository<CourseScheduling, String> {
        List<CourseScheduling> findByAssignedCourse_Teacher_IdAndAssignedCourse_ObsoleteFalse(
                        String teacherId);

        List<CourseScheduling> findByAssignedCourse_ObsoleteFalseAndAssignedCourse_Ue_Level_Id(String levelId);

        Page<CourseScheduling> findByAssignedCourse_ObsoleteFalseAndAssignedCourse_Ue_Level_Branch_Id(
                        String branchId,
                        Pageable page);

        List<CourseScheduling> findByTimetableUsedTrueAndTimeSlot(CourseTimeSlot timeSlot);
}