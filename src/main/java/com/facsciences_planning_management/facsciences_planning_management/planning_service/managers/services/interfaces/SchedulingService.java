package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;

public interface SchedulingService<T extends SchedulingDTO> {
    T createScheduling(T request);

    List<T> getSchedulesByRoom(String roomId);

    List<T> getSchedulesByTeacherOrProctor(String userId);

    List<T> getSchedulesByLevel(String levelId);

    Page<T> getScheduleByBranch(String branchId, Pageable page);

    List<T> getSchedulesByTimetable(String timetableId);

    List<T> getSchedulesByTimeSlot(String timeSlot);

    void deleteScheduling(String id);

    T updateScheduling(String id,
            T request);

    List<TimeSlotDTO> getTimeSlots();
}