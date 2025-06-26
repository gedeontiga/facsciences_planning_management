package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Scheduling;

@Repository
public interface SchedulingRepository<T extends Scheduling, ID> extends
        MongoRepository<T, ID> {
    List<T> findByRoomIdAndTimetableUsedTrue(String roomId);

    List<T> findByTimetableId(String timetableId);
}