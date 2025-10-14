package com.facsciencesuy1.planning_management.planning_service.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Scheduling;

@Repository
public interface SchedulingRepository<T extends Scheduling, ID> extends
        MongoRepository<T, ID> {
    List<T> findByRoomIdAndTimetableUsedTrue(String roomId);

    List<T> findByTimetableId(String timetableId);
}