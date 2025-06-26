package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import java.util.List;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.ExamTimeSlot;

@Repository
public interface ExamSchedulingRepository extends
        SchedulingRepository<ExamScheduling, String> {

    List<ExamScheduling> findByTimetableUsedTrueAndTimeSlot(ExamTimeSlot timeSlot);

    Page<ExamScheduling> findByTimetableUsedTrueAndUe_Level_Branch_Id(String branchId, Pageable page);

    List<ExamScheduling> findByTimetableUsedTrueAndUe_Level_Id(String levelId);

    List<ExamScheduling> findByTimetableUsedTrueAndProctorId(String proctorId);
}
