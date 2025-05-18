package com.facsciences_planning_management.facsciences_planning_management.planning_service.models.repositories;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Scheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.types.SessionType;

@Repository
public interface SchedulingBaseRepository<T extends Scheduling, ID> extends MongoRepository<T, ID> {

        List<T> findByRoomId(String roomId);

        List<T> findByUeIdIn(List<String> ueIds);

        List<T> findByPlanningId(String planningId);

        List<T> findBySessionType(SessionType sessionType);

        List<T> findByRoomIdAndStartTimeGreaterThanEqualAndStartTimeLessThan(
                        String roomId, LocalTime start, LocalTime end);

        List<T> findByRoomIdAndEndTimeGreaterThanAndEndTimeLessThanEqual(
                        String roomId, LocalTime start, LocalTime end);

        List<T> findByRoomIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        String roomId, LocalTime start, LocalTime end);
}