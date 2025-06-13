package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ManualExamSchedulingRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ManualSchedulingDetailsDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ManualCourseSchedulingRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingDTO;

public interface AdminSchedulingService {

    CourseSchedulingDTO createManualSimpleScheduling(ManualCourseSchedulingRequestDTO request);

    ExamSchedulingDTO createManualExamScheduling(ManualExamSchedulingRequestDTO request);

    void assignCourseToTeacher(String courseId, String teacherId,
            Optional<ManualSchedulingDetailsDTO> schedulingDetails);

}