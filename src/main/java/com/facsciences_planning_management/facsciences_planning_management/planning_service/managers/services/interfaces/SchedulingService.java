package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingCreateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingUpdateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingConflictCheckRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingCreateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingUpdateRequest;

public interface SchedulingService {
    SimpleSchedulingDTO createSimpleScheduling(SimpleSchedulingCreateRequest request);

    ExamSchedulingDTO createExamScheduling(ExamSchedulingCreateRequest request);

    List<SchedulingDTO> getSchedulesByRoom(String roomId);

    List<SchedulingDTO> getSchedulesForTeacher(String teacherId);

    List<SchedulingDTO> getSchedulesForLevel(String levelId);

    void deleteScheduling(String id, SessionType type);

    SimpleSchedulingDTO updateSimpleScheduling(String id, SimpleSchedulingUpdateRequest request);

    ExamSchedulingDTO updateExamScheduling(String id, ExamSchedulingUpdateRequest request);

    boolean checkForSchedulingConflicts(SchedulingConflictCheckRequest request);
}